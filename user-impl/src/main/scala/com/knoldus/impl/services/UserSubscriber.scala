
package com.knoldus.impl.services

import akka.Done
import akka.stream.scaladsl.Flow
import com.knoldus.api.datamodels.UserReceived
import com.knoldus.api.services.UserService
import com.knoldus.impl.eventsourcing.{AddUser, UserEntity}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntity, PersistentEntityRegistry}
import com.typesafe.scalalogging.Logger

class UserSubscriber(userService: UserService,persistentEntityRegistry: PersistentEntityRegistry) {

  private final val log = Logger(classOf[UserSubscriber])

  userService
    .userEvents
    .subscribe
    .atLeastOnce(
      Flow[UserReceived].map(msg => {
        log.info("consume the user from topic" /*+ msg.user*/)
        val ref = persistentEntityRegistry.refFor[UserEntity](msg.user.id)
        ref.ask(AddUser(msg.user))
        Done
      }
      )
    )
}
