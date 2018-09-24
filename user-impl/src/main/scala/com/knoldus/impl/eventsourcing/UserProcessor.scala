
package com.knoldus.impl.eventsourcing

import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import scala.concurrent.ExecutionContext

class UserProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[UserEvent] {

  val userRepository = new UserRepository(session)

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[UserEvent] = {
    readSide.builder[UserEvent]("userEventOffset")
      .setGlobalPrepare(userRepository.createTable)
      .setPrepare(_ => userRepository.preparedStatements())
      .setEventHandler[UserAdded](e => userRepository.storeUser(e.event.user))
     // .setEventHandler[UserDeleted](e => userRepository.deleteUser(e.event.id))
      .build()
  }

  def aggregateTags: AggregateEventTag[UserEvent]=
    UserEvent.Tag
}