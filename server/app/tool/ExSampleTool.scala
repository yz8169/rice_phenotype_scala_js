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
trait ExSampleToolTrait {

  def selectAll(implicit request: RequestHeader): Future[Seq[ExIntroductionRow]]

  def selectAllNumber(implicit request: RequestHeader):Future[Seq[String]]

}

trait ExSampleTool extends ExSampleToolTrait {

  val exIntroductionDao: ExIntroductionDao

  override def selectAll(implicit request: RequestHeader) = {
    exIntroductionDao.selectAll
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    exIntroductionDao.selectAllNumber
  }


}

trait ExSampleToolWithLimit extends ExSampleToolTrait {
  val exIntroductionDao: ExIntroductionDao
  val userLimitDao: UserLimitDao

  override def selectAll(implicit request: RequestHeader) = {
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).flatMap { user =>
      val numbers = user.exIntroductionSample.split(";")
      exIntroductionDao.selectAll(numbers)
    }
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).map { user =>
      val numbers = user.exIntroductionSample.split(";")
     numbers
    }
  }


}


