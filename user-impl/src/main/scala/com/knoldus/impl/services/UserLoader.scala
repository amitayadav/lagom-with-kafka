
package com.knoldus.impl.services

import com.knoldus.api.services.{ExternalService, UserService}
import com.knoldus.impl.eventsourcing.{UserEntity, UserProcessor, UserRepository}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class UserLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new UserApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new UserApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[UserService])
}

abstract class UserApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val lagomServer = serverFor[UserService](wire[UserServiceImpl])

  lazy val externalService: ExternalService = serviceClient.implement[ExternalService]

  override lazy val jsonSerializerRegistry = UserSerializerRegistry

  persistentEntityRegistry.register(wire[UserEntity])

  lazy val userService = serviceClient.implement[UserService]

  wire[UserSubscriber]

  lazy val repository: UserRepository = wire[UserRepository]

  readSide.register(wire[UserProcessor])

}






