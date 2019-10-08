package dao

import javax.inject.Inject
import models.Tables.LocalSampleRow
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import models.Tables._

/**
  * Created by yz on 2019/1/18
  */
class LocalSampleDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insertAll(rows: Seq[LocalSampleRow]): Future[Unit] = {
    db.run(LocalSample ++= rows).map(_ => ())
  }

  def insert(row: LocalSampleRow) = {
    db.run(LocalSample += row).map(_ => ())
  }

  def update(row: LocalSampleRow) = {
    db.run(LocalSample.filter(_.number === row.number).update(row)).map(_ => ())
  }

  def insertOrUpdates(rows: Seq[LocalSampleRow]) = {
    val action = {
      val numbers = rows.map(_.number)
      val delete = LocalSample.filter(_.number.inSetBind(numbers)).delete
      val insertAll = LocalSample ++= rows
      delete.flatMap(_ => insertAll)
    }.transactionally
    db.run(action).map(_ => ())
  }

  def selectAll = db.run(LocalSample.result)

  def selectAll(numbers:Seq[String]) = db.run(LocalSample.
    filter(_.number.inSetBind(numbers)).result)

  def selectAllNumber = db.run(LocalSample.map(_.number).result)

  def deleteByNumber(number: String) = db.run(LocalSample.filter(_.number === number).delete).map(_ => ())

  def deleteByNumbers(numbers: Seq[String]) = db.run(LocalSample.filter(_.number.inSetBind(numbers)).
    delete).map(_ => ())

  def update(number: String, photo: String) = {
    db.run(LocalSample.filter(_.number === number).map(_.photo).update(photo)).map(_ => ())
  }

  def selectByNumber(number: String) = db.run(LocalSample.
    filter(_.number === number).result.head)

  def selectByNumberOp(number: String) = db.run(LocalSample.
    filter(_.number === number).result.headOption)


}
