package controllers

import dao._
import javax.inject.Inject
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import tool.{BreedSampleTool, BreedSampleToolWithLimit, FormTool, Tool}
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/3/12
  */
class BreedSampleController @Inject()(cc: ControllerComponents, val breedSampleDao: BreedSampleDao,
                                      val userLimitDao: UserLimitDao,
                                      formTool:FormTool) extends AbstractController(cc) with
  BreedSampleTool with BreedSampleToolWithLimit{

  def viewBefore = Action { implicit request =>
    Ok(views.html.breedSample.view())
  }

  def getAllFiles = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    breedSampleDao.selectByNumber(data.number).map { x =>
      val photo = x.photo
      val json = Utils.str2Json(x.photo)
      Ok(json)
    }

  }

  def getSamplePhenotypeInfo = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    breedSampleDao.selectByNumber(data.number).map { x =>
      val map = Utils.getMapByT(x)
      Ok(Json.toJson(map))
    }
  }

  def getAllPhenotype = Action.async { implicit request =>
    selectAll.map { rows =>
      val phenotypeNames = Shared.breedSamplePhenotypeNames
      val array = rows.map { row =>
        val map = Utils.str2Map(row.phenotype)
        val newMap = phenotypeNames.map { x =>
          (x, map.getOrElse(x, ""))
        }.toMap
        Map("number" -> row.number, "name" -> row.name, "comment" -> row.comment) ++ newMap
      }
      val json = Json.toJson(array)
      Ok(json)
    }

  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.breedSample.detailInfo(data.number))
  }

  def searchBefore = Action { implicit request =>
    Ok(views.html.breedSample.search())
  }

  def statBefore = Action { implicit request =>
    Ok(views.html.breedSample.stat())
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
