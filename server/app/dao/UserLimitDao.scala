package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tool.Pojo.UserData
//import tool.UserData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2018/7/17
  */
class UserLimitDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectAll= db.run(UserLimit.result)

//  def selectByName(name: String): Future[Option[UserRow]] = db.run(User.
//    filter(_.name === name).result.headOption)
//
//  def selectByNameOrEmail(name: String): Future[Option[UserRow]] = db.run(User.
//    filter(x => x.name === name ).result.headOption)
//
//  def selectSomeByName(name: String): Future[UserRow] = db.run(User.
//    filter(_.name === name).result.head)
//
//
//  def selectByUserData(user: UserData): Future[Option[UserRow]] = db.run(User.
//    filter(x => x.name === user.name).
//    filter(_.password === user.password).result.headOption)

  def insert(row: UserLimitRow) = db.run(UserLimit += row).map(_ => ())

//  def insertAndReturning(row: UserRow) = db.run(User.returning(User.map(_.id)) += row)

  def deleteById(id: Int): Future[Unit] = db.run(UserLimit.filter(_.id === id).delete).map(_ => ())

//  def selectById(id: Int): Future[UserRow] = db.run(User.
//    filter(_.id === id).result.head)

  def update(row: UserLimitRow): Future[Unit] = db.run(UserLimit.filter(_.id === row.id).update(row)).
    map(_ => ())

//  def update(row: UserRow): Future[Unit] = db.run(User.filter(_.id === row.id).
//    update(row)).map(_ => ())



}
