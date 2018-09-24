
package com.knoldus.impl.services

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.knoldus.api.datamodels
import com.knoldus.api.datamodels.User
import com.knoldus.api.services.{ExternalService, UserService}
import com.knoldus.impl.eventsourcing._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, NotFound, TransportErrorCode}
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.typesafe.scalalogging.Logger

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(session:CassandraSession,actorSystem: ActorSystem, persistentEntityRegistry: PersistentEntityRegistry,
                      externalService: ExternalService)(implicit val mat: Materializer,
                                                        executionContext: ExecutionContext) extends UserService {

  private final val log = Logger(classOf[UserServiceImpl])

  override def getUserFromCassandra(id:String) = ServiceCall { _ =>
    session.selectOne("SELECT * FROM usertable WHERE id =?", id).map {
      case Some(row) => User.apply(row.getString("id"),row.getString("name"), row.getString("gender"))
      case None => throw new NotFound(TransportErrorCode.NotFound, new ExceptionMessage("User Id Not Found",
        "User with this user id does not exist"))
    }
  }

  /*override def deleteUserFromCassandra(id: String): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    persistentEntityRegistry.refFor[UserEntity](id)
      .ask(DeleteUser(id)).map {_=>
       log.info(s"User with book id $id successfully deleted.")
        Done.getInstance()
      }
  }*/


  actorSystem.scheduler.schedule(0.microseconds, interval = 300.seconds){
    val replyType= getUserFromExternalService
    replyType.invoke().map(e => e match {
      case Done => userEvents
      case _ => log.info("no user")
    })
  }

  Done.getInstance()

  override def getUserFromExternalService = ServiceCall{ _=>
    val result: Future[User] = externalService.getUser.invoke()
    result.map(user => {
      /*val ref = persistentEntityRegistry.refFor[UserEntity](user.id)
      val replyType= ref.ask(AddUser(user))
        .map(_ => {*/
          log.info("new user added")
          Done
        })
      /*Done.getInstance()
    })*/

  }

  override def userEvents: Topic[datamodels.UserReceived] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        log.info("inside userProducer")
        persistentEntityRegistry.eventStream(UserEvent.Tag, fromOffset)
          .map(event =>
            (convertEvent(event), event.offset))
    }

  private def convertEvent(value: EventStreamElement[UserEvent]): datamodels.UserReceived = {
    value.event match {
      case UserAdded(user) => log.info("inside userProducer convert method")
        datamodels.UserReceived(user)
    }
  }
}

