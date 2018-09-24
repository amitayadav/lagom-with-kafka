package com.knoldus.api.services

import akka.{Done, NotUsed}
import com.knoldus.api.datamodels.{User, UserReceived}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait UserService extends Service {

  override final def descriptor = {
    import Service._

    named("user")
      .withCalls(
       // restCall(Method.DELETE,"/api/user/:id", deleteUserFromCassandra _),
        restCall(Method.GET,"/api/consume/user", getUserFromCassandra _)
      )
      .withTopics(
        topic("getting-UserEvent", userEvents)
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[UserReceived](_.user.id)
          )
      )
      .withAutoAcl(true)

  }

  def getUserFromCassandra(id:String): ServiceCall[NotUsed, User]

  def userEvents: Topic[UserReceived]

  def getUserFromExternalService: ServiceCall[NotUsed, Done]

  //def deleteUserFromCassandra(id:String):ServiceCall[NotUsed, Done]


}
