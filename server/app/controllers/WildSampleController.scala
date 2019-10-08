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
class WildSampleController @Inject()(cc: ControllerComponents, val wildSampleDao: WildSampleDao,
                                     val userLimitDao: UserLimitDao,
                                     formTool: FormTool) extends
  AbstractController(cc) with WildSampleTool with WildSampleToolWithLimit {

  def getAllPhenotype = Action.async { implicit request =>
   selectAll.map { rows =>
      val phenotypeNames = Shared.wildSamplePhenotypeNames
      val array = rows.map { row =>
        val map = Utils.str2Map(row.phenotype)
        val newMap = phenotypeNames.map { x =>
          (x, map.getOrElse(x, ""))
        }.toMap
        Map("number" -> row.number, "source" -> row.source, "comment" -> row.comment) ++ newMap
      }
      val json = Json.toJson(array)
      Ok(json)
    }

  }

  def getSamplePhenotypeInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    wildSampleDao.selectByNumber(data.number).map { x =>
      val map = Utils.getMapByT(x)
      Ok(Json.toJson(map))
    }
  }

  def getAllFiles = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    wildSampleDao.selectByNumber(data.number).map { x =>
      val photo = x.photo
      val json = Utils.str2Json(x.photo)
      Ok(json)
    }

  }

  def viewBefore = Action { implicit request =>
    Ok(views.html.wildSample.view())
  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.wildSample.detailInfo(data.number))
  }

  def searchBefore = Action { implicit request =>
    Ok(views.html.wildSample.search())
  }

  def statBefore = Action { implicit request =>
    Ok(views.html.wildSample.stat())
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
      val array = rows.map { row =>
        val map = Utils.str2Map(row.phenotype)
        Map("number" -> row.number, data.phenotype -> map.getOrElse(phenotype, ""))
      }.filter { map =>
        val b = Tool.validByNumbers(data.numbers, map)
        StringUtils.isNotBlank(map(phenotype)) && b
      }
      val json = Json.toJson(array)
      Ok(json)
    }

  }


}
