package controllers.adminC

import java.io.File

import controllers.adminC
import dao._
import javax.inject.Inject
import models.Tables._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc._
import shared.Shared
import tool.{FormTool, Tool}
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by yz on 2019/3/6
  */
class BreedSampleController @Inject()(cc: ControllerComponents, localSampleDao: LocalSampleDao,
                                      formTool: FormTool, tool: Tool, breedSampleDao: BreedSampleDao) extends AbstractController(cc) {

  def addSamplePhenotypeByFileBefore = Action { implicit request =>
    Ok(views.html.admin.breedSample.addSamplePhenotypeByFile())
  }

  def checkFile(file: File) = {
    var error = ""
    (true, error)
  }

  def addSamplePhenotypeByFile = Action.async(parse.multipartFormData) { request =>
    val file = request.body.file("file").get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val tmpXlsxFile = new File(tmpDir, "tmp.xlsx")
    file.ref.moveTo(tmpXlsxFile, true)
    val bm = checkFile(tmpXlsxFile)
    if (!bm._1) {
      tool.deleteDirectory(tmpDir)
      Future.successful(Ok(Json.obj("error" -> bm._2)))
    } else {
      val phenotypeNames = Shared.breedSamplePhenotypeNames
      val lines = Utils.xlsx2Lines(tmpXlsxFile)
      val headers = lines.head.split("\t")
      val rows = lines.drop(1).map { line =>
        val columns = line.split("\t")
        headers.zip(columns).toMap
      }.filter { map =>
        val id = map("保存号")
        StringUtils.isNotEmpty(id)
      }.groupBy { map =>
        map("保存号")
      }.mapValues { maps =>
        maps.last
      }.map { case (id, map) =>
        map
      }.toList.map { map =>
        val phenotypeMap = map.filter { case (key, value) =>
          phenotypeNames.contains(key)
        }
        BreedSampleRow(map("保存号"), map("名称"), Json.toJson(phenotypeMap).toString(),
          map.getOrElse("备注", ""), "")
      }
      breedSampleDao.insertOrUpdates(rows).map { x =>
        tool.deleteDirectory(tmpDir)
        Ok("success!")
      }
    }

  }

  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    breedSampleDao.selectByNumberOp(data.number).map {
      case Some(y) => Ok(Json.obj("valid" -> false))
      case None => Ok(Json.obj("valid" -> true))
    }
  }

  def addSamplePhenotypeBefore = Action { implicit request =>
    Ok(views.html.admin.breedSample.addSamplePhenotype())
  }

  def addSamplePhenotype = Action.async { implicit request =>
    val data = formTool.breedSampleForm.bindFromRequest().get
    val row = BreedSampleRow(data.number, data.name, data.phenotype, data.comment, "")
    breedSampleDao.insert(row).map { x =>
      Ok("success!")
    }
  }

  def updateSamplePhenotype = Action.async { implicit request =>
    val data = formTool.breedSampleForm.bindFromRequest().get
    breedSampleDao.selectByNumber(data.number).flatMap { row =>
      val newRow = row.copy(data.number, data.name, data.phenotype, data.comment)
      breedSampleDao.update(newRow)
    }.map { x =>
      Ok("success!")
    }
  }

  def updateSamplePhenotypeBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.breedSample.updateSamplePhenotype(data.number))
  }

  def deleteAppendix = Action.async { implicit request =>
    val data = formTool.imageForm.bindFromRequest().get
    val file = new File(Utils.appendixDir, s"${data.dir}/${data.number}/${data.fileName}")
    FileUtils.deleteQuietly(file)
    breedSampleDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo)
      val newMap = map - data.fileName
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      breedSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def updateDesc = Action.async { implicit request =>
    val data = formTool.descForm.bindFromRequest().get
    breedSampleDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo) + (data.fileName -> data.describe)
      val newRow = x.copy(photo = Json.toJson(map).toString())
      breedSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }

  }

  def addAppendix = Action.async(parse.multipartFormData) { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val photoMap = request.body.files.map { file =>
      val finalFile = new File(Utils.breedSampleADir, data.number + File.separator + file.filename)
      FileUtils.writeStringToFile(finalFile, "")
      file.ref.moveTo(finalFile, true)
      (file.filename, "")
    }.toMap

    breedSampleDao.selectByNumber(data.number).flatMap { x =>
      val befPhoto = x.photo
      val map = Utils.str2Map(befPhoto)
      val newMap = map ++ photoMap
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      breedSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))

    }
  }

  def manageAppendixBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.breedSample.manageAppendix(data.number))
  }

  def manageSamplePhenotypeBefore = Action { implicit request =>
    Ok(views.html.admin.breedSample.manageSamplePhenotype())
  }

  def deleteSamplePhenotypes = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    breedSampleDao.deleteByNumbers(data.numbers).map { _ =>
      data.numbers.foreach { x =>
        val file = new File(Utils.breedSampleADir, x)
        FileUtils.deleteQuietly(file)
      }
      Ok("success")
    }
  }

  def deleteByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    breedSampleDao.deleteByNumber(data.number).map { x =>
      val appendixDir = new File(Utils.breedSampleADir, data.number)
      Utils.deleteDirectory(appendixDir)
      Ok("success")
    }
  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.breedSample.detailInfo(data.number))
  }

  def getAllPhenotype = Action.async { implicit request =>
    breedSampleDao.selectAll.map { rows =>
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


}
