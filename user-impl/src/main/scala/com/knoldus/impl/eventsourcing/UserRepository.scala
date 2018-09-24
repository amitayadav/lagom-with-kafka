package com.knoldus.impl.eventsourcing

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.knoldus.api.datamodels.User
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession

import scala.concurrent.{ExecutionContext, Future}

class UserRepository(session: CassandraSession)(implicit ec: ExecutionContext) {

  private var insertUser: PreparedStatement = _
 // private var deleteUser: PreparedStatement = _


  def createTable(): Future[Done] = {
    session.executeCreateTable(
      """
        |CREATE TABLE IF NOT EXISTS usertable(
        |id text PRIMARY KEY,
        |name text,
        |gener text
        |);
      """.stripMargin)
  }

  def preparedStatements(): Future[Done] =
    session.prepare("INSERT INTO usertable(id, name, gender) VALUES (?, ?, ?)")
      .map { ps =>
        insertUser = ps
        Done
      }/*.map(_ => session.prepare("DELETE FROM usertable where id = ?").map(ps => {
      deleteUser = ps
      Done
    })).flatten*/

  def storeUser(user:User): Future[List[BoundStatement]] = {
    val userBindStatement = insertUser.bind()
    userBindStatement.setString("id", user.id)
    userBindStatement.setString("name", user.name)
    userBindStatement.setString("gender", user.gender)
    Future.successful(List(userBindStatement))
  }

  /*def deleteUser(id: String): Future[List[BoundStatement]] = {
    val bindDeleteUser: BoundStatement = deleteUser.bind()
    bindDeleteUser.setString("id", id)
    Future.successful(List(bindDeleteUser))
  }*/
}
