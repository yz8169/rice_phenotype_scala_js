package tool

import dao._
import javax.inject.Inject
import models.Tables
import models.Tables.{UserLimitRow, UserRow}
import org.joda.time.DateTime
import play.api.data
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.BasicProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by Administrator on 2019/9/27
 */

trait AdminToolTrait {

  def insertUser(row: UserRow): Future[Unit]

  def deleteById(id: Int): Future[Unit]

}

trait AdminTool extends AdminToolTrait {

  val userDao: UserDao

  override def insertUser(row: Tables.UserRow): Future[Unit] = {
    userDao.insertAndReturning(row).map(_ => ())
  }

  override def deleteById(id: Int): Future[Unit] = {
    userDao.deleteById(id)
  }

}

trait AdminToolWithLimit extends AdminToolTrait {
  val userDao: UserDao
  val userLimitDao: UserLimitDao

  override def insertUser(row: Tables.UserRow): Future[Unit] = {
    userDao.insertAndReturning(row).flatMap { userId =>
      val userLimitRow = UserLimitRow(userId, "", "", "", "")
      userLimitDao.insert(userLimitRow)
    }
  }

  override def deleteById(id: Int): Future[Unit] = {
    userDao.deleteById(id).zip(userLimitDao.deleteById(id)).map(_ => ())
  }

}

