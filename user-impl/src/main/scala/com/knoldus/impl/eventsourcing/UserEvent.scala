
package com.knoldus.impl.eventsourcing

import com.knoldus.api.datamodels.User
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

sealed trait UserEvent extends AggregateEvent[UserEvent] {
  def aggregateTag = UserEvent.Tag
}

object UserEvent {
  //val Tag:AggregateEventShards[UserEvent] = AggregateEventTag.sharded[UserEvent](4)
  val Tag = AggregateEventTag[UserEvent]
}

case class UserAdded(user: User) extends UserEvent

object UserAdded {

  implicit val format: Format[UserAdded] = Json.format[UserAdded]
}

/*
case class UserDeleted(id: String) extends UserEvent

object UserDeleted {

  implicit val format: Format[UserDeleted] = Json.format[UserDeleted]
}
*/
