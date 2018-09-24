package com.knoldus.api.datamodels

import play.api.libs.json.{Format, Json}

case class User(id: String, name: String, gender: String)

object User {

  implicit val format: Format[User] = Json.format[User]

}