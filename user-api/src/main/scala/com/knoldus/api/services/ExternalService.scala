
package com.knoldus.api.services

import akka.NotUsed
import com.knoldus.api.datamodels.User
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait ExternalService extends Service {

  override final def descriptor = {
    import Service._

    named("external-service")
      .withCalls(
        pathCall("/amitayadav/56f2c63c6c0335d0bd23d97793713ebf/raw/e5e71219a3a8836675553f70d890152d35daae5b/user.json", getUser _)
      ).withAutoAcl(true)
  }

  def getUser: ServiceCall[NotUsed, User]

}
