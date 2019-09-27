package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2019/1/18
  */
class BreedSampleDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[BreedSampleRow]) = {
    db.run(BreedSample ++= rows).map(_ => ())
  }

  def insert(row: BreedSampleRow) = {
    db.run(BreedSample += row).map(_ => ())
  }

  def update(row: BreedSampleRow) = {
    db.run(BreedSample.filter(_.number === row.number).update(row)).map(_ => ())
  }

  def insertOrUpdates(rows: Seq[BreedSampleRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val delete = BreedSample.filter(_.number.inSetBind(numbers)).delete
      val insertAll = BreedSample ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }

  def selectAll = db.run(BreedSample.result)

  def deleteByNumber(number: String) = db.run(BreedSample.filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(numbers: Seq[String]) = db.run(BreedSample.filter(_.number.inSetBind(numbers)).
    delete).map(_ => ())

  def update(number: String, photo: String) = {
    db.run(BreedSample.filter(_.number === number).map(_.photo).update(photo)).map(_ => ())
  }

  def selectByNumber(number: String) = db.run(BreedSample.
    filter(_.number === number).result.head)

  def selectByNumberOp(number: String) = db.run(BreedSample.
    filter(_.number === number).result.headOption)

  def selectAllNumber = db.run(BreedSample.map(_.number).result)


}
