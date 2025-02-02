package controllers

import dao._
import javax.inject.Inject
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import tool._
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/5/8
  */
class ExIntroductionController @Inject()(cc: ControllerComponents, val exIntroductionDao: ExIntroductionDao,
                                         val userLimitDao: UserLimitDao,
                                         formTool:FormTool,wildSampleDao: WildSampleDao) extends
  AbstractController(cc) with ExSampleTool with ExSampleToolWithLimit{

  def getAllPhenotype = Action.async { implicit request =>
    selectAll.map { rows =>
      val phenotypeNames = Shared.exIntroductionPhenotypeNames
      val array=Utils.getArrayByTs(rows,"phenotype")
      val json = Json.toJson(array)
      Ok(json)
    }

  }

  def getSamplePhenotypeInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    exIntroductionDao.selectByNumber(data.number).map { x =>
      val map = Utils.getMapByT(x)
      Ok(Json.toJson(map))
    }
  }

  def getAllFiles = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    exIntroductionDao.selectByNumber(data.number).map { x =>
      val photo = x.photo
      val json = Utils.str2Json(x.photo)
      Ok(json)
    }

  }

  def viewBefore = Action { implicit request =>
    Ok(views.html.exIntroduction.view())
  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.exIntroduction.detailInfo(data.number))
  }

  def searchBefore = Action { implicit request =>
    Ok(views.html.exIntroduction.search())
  }

  def statBefore = Action { implicit request =>
    Ok(views.html.exIntroduction.stat())
  }

  def getAllNumber = Action.async { implicit request =>
    selectAllNumber.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getStatData = Action.async { implicit request =>
    val data = formTool.statForm.bindFromRequest().get
    val phenotype = data.phenotype
    selectAll.map { rows =>
      val array = Utils.getArrayByTs(rows,"phenotype").filter { map =>
        val b = Tool.validByNumbers(data.numbers, map)
        StringUtils.isNotBlank(map(phenotype)) && b
      }
      val json = Json.toJson(array)
      Ok(json)
    }

  }



}
