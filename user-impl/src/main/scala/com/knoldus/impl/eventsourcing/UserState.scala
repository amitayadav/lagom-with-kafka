package com.knoldus.impl.eventsourcing

import com.knoldus.api.datamodels.User
import play.api.libs.json.{Format, Json}

case class UserState(user: Option[User], timestamp: String)

object UserState {

  implicit val format: Format[UserState] = Json.format
}

