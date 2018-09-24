package com.knoldus.api.datamodels

import play.api.libs.json.{Format, Json}

case class UserReceived(user: User)

object UserReceived {

  implicit val format: Format[UserReceived] = Json.format[UserReceived]
}
