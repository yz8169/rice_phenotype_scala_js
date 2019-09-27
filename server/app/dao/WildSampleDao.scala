package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2019/1/18
  */
class WildSampleDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[WildSampleRow]) = {
    db.run(WildSample ++= rows).map(_ => ())
  }

  def insert(row: WildSampleRow) = {
    db.run(WildSample += row).map(_ => ())
  }

  def update(row: WildSampleRow) = {
    db.run(WildSample.filter(_.number === row.number).update(row)).map(_ => ())
  }

  def insertOrUpdates(rows: Seq[WildSampleRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val delete = WildSample.filter(_.number.inSetBind(numbers)).delete
      val insertAll = WildSample ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }

  def selectAll = db.run(WildSample.result)

  def deleteByNumber(number: String) = db.run(WildSample.filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(numbers: Seq[String]) = db.run(WildSample.filter(_.number.inSetBind(numbers)).
    delete).map(_ => ())

  def update(number: String, photo: String) = {
    db.run(WildSample.filter(_.number === number).map(_.photo).update(photo)).map(_ => ())
  }

  def selectByNumber(number: String) = db.run(WildSample.
    filter(_.number === number).result.head)

  def selectByNumberOp(number: String) = db.run(WildSample.
    filter(_.number === number).result.headOption)

  def selectAllNumber = db.run(WildSample.map(_.number).result)



}
