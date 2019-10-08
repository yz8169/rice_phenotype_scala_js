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
trait LocalSampleToolTrait {

  def selectAll(implicit request: RequestHeader): Future[Seq[LocalSampleRow]]

  def selectAllNumber(implicit request: RequestHeader):Future[Seq[String]]

}

trait LocalSampleToolTool extends LocalSampleToolTrait {

  val localSampleDao: LocalSampleDao

  override def selectAll(implicit request: RequestHeader): Future[Seq[LocalSampleRow]] = {
    localSampleDao.selectAll
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    localSampleDao.selectAllNumber
  }


}

trait LocalSampleToolWithLimit extends LocalSampleToolTrait {
  val localSampleDao: LocalSampleDao
  val userLimitDao: UserLimitDao

  override def selectAll(implicit request: RequestHeader): Future[Seq[LocalSampleRow]] = {
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).flatMap { user =>
      val numbers = user.localSample.split(";")
      localSampleDao.selectAll(numbers)
    }
  }

  override def selectAllNumber(implicit request: RequestHeader)={
    val userId = Tool.getUserId
    userLimitDao.selectById(userId).map { user =>
      val numbers = user.localSample.split(";")
     numbers
    }
  }


}


