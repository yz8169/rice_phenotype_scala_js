package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2019/1/18
  */
class ExIntroductionDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[ExIntroductionRow]) = {
    db.run(ExIntroduction ++= rows).map(_ => ())
  }

  def insert(row: ExIntroductionRow) = {
    db.run(ExIntroduction += row).map(_ => ())
  }

  def update(row: ExIntroductionRow) = {
    db.run(ExIntroduction.filter(_.number === row.number).update(row)).map(_ => ())
  }

  def insertOrUpdates(rows: Seq[ExIntroductionRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val delete = ExIntroduction.filter(_.number.inSetBind(numbers)).delete
      val insertAll = ExIntroduction ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }

  def selectAll = db.run(ExIntroduction.result)

  def deleteByNumber(number: String) = db.run(ExIntroduction.filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(numbers: Seq[String]) = db.run(ExIntroduction.filter(_.number.inSetBind(numbers)).
    delete).map(_ => ())

  def update(number: String, photo: String) = {
    db.run(ExIntroduction.filter(_.number === number).map(_.photo).update(photo)).map(_ => ())
  }

  def selectByNumber(number: String) = db.run(ExIntroduction.
    filter(_.number === number).result.head)

  def selectByNumberOp(number: String) = db.run(ExIntroduction.
    filter(_.number === number).result.headOption)

  def selectAllNumber = db.run(ExIntroduction.map(_.number).result)


}
