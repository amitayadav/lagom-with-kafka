
package com.knoldus.impl.eventsourcing

import akka.Done
import com.knoldus.api.datamodels.User
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait UserCommand[R] extends ReplyType[R]

case class AddUser(user: User) extends UserCommand[Done]

object AddUser {
  implicit val format: Format[AddUser] = Json.format[AddUser]
}

case class GetUser(id: String) extends UserCommand[User]

object GetUser {
  implicit val format: Format[GetUser] = Json.format
}

/*case class DeleteUser(id: String) extends UserCommand[Done]

object DeleteUser {
  implicit val format: Format[DeleteUser] = Json.format
}*/
