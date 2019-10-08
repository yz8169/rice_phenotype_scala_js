package tool

import dao._
import models.Tables
import models.Tables._
import play.api.mvc.RequestHeader

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by Administrator on 2019/10/8
 */
trait BreedSampleToolTrait {

  def selectAll(implicit request: RequestHeader): Future[Seq[BreedSampleRow]]

  def selectAllNumber(implicit request: RequestHeader):Future[Seq[String]]

}

trait BreedSampleTool extends BreedSampleToolTrait {

  val breedSampleDao: BreedSampleDao

  override def selectAll(implicit request: RequestHeader) = {
    breedSampleDao.selectAll
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    breedSampleDao.selectAllNumber
  }


}

trait BreedSampleToolWithLimit extends BreedSampleToolTrait {
  val breedSampleDao: BreedSampleDao
  val userLimitDao: UserLimitDao

  override def selectAll(implicit request: RequestHeader) = {
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).flatMap { user =>
      val numbers = user.breedSample.split(";")
      breedSampleDao.selectAll(numbers)
    }
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).map { user =>
      val numbers = user.breedSample.split(";")
     numbers
    }
  }


}


