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
trait WildSampleToolTrait {

  def selectAll(implicit request: RequestHeader): Future[Seq[WildSampleRow]]

  def selectAllNumber(implicit request: RequestHeader):Future[Seq[String]]

}

trait WildSampleTool extends WildSampleToolTrait {

  val wildSampleDao: WildSampleDao

  override def selectAll(implicit request: RequestHeader) = {
    wildSampleDao.selectAll
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    wildSampleDao.selectAllNumber
  }


}

trait WildSampleToolWithLimit extends WildSampleToolTrait {
  val wildSampleDao: WildSampleDao
  val userLimitDao: UserLimitDao

  override def selectAll(implicit request: RequestHeader) = {
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).flatMap { user =>
      val numbers = user.wildSample.split(";")
      wildSampleDao.selectAll(numbers)
    }
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).map { user =>
      val numbers = user.wildSample.split(";")
     numbers
    }
  }


}


