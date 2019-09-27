package controllers.adminC

import java.io.File

import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import tool.{FormTool, Tool}
import utils.Utils
import models.Tables._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2019/5/5
  */
class WildSampleController @Inject()(cc: ControllerComponents, wildSampleDao: WildSampleDao,
                                     formTool: FormTool, tool: Tool) extends AbstractController(cc) {

  def addSamplePhenotypeBefore = Action { implicit request =>
    Ok(views.html.admin.wildSample.addSamplePhenotype())
  }

  def addSamplePhenotypeByFileBefore = Action { implicit request =>
    Ok(views.html.admin.wildSample.addSamplePhenotypeByFile())
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
      val phenotypeNames = Shared.wildSamplePhenotypeNames
      val lines = Utils.xlsx2Lines(tmpXlsxFile)
      val headers = lines.head.split("\t")
      val rows = lines.drop(1).map { line =>
        val columns = line.split("\t")
        headers.zip(columns).toMap
      }.filter { map =>
        val id = map("原统一编号")
        StringUtils.isNotEmpty(id)
      }.groupBy { map =>
        map("原统一编号")
      }.mapValues { maps =>
        maps.last
      }.map { case (id, map) =>
        map
      }.toList.map { map =>
        val phenotypeMap = map.filter { case (key, value) =>
          phenotypeNames.contains(key)
        }
        WildSampleRow(map("原统一编号"), map("采集/原产地"), Json.toJson(phenotypeMap).toString(),
          map.getOrElse("备注", ""), "")
      }

      wildSampleDao.insertOrUpdates(rows).map { x =>
        tool.deleteDirectory(tmpDir)
        Ok("success!")
      }
    }
  }

  def checkFile(file: File) = {
    var error = ""
    (true, error)
  }

  def manageSamplePhenotypeBefore = Action { implicit request =>
    Ok(views.html.admin.wildSample.manageSamplePhenotype())
  }

  def deleteByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    wildSampleDao.deleteByNumber(data.number).map { x =>
      val appendixDir = new File(Utils.wildSampleADir, data.number)
      Utils.deleteDirectory(appendixDir)
     Ok("success!")
    }
  }

  def deleteSamplePhenotypes = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    wildSampleDao.deleteByNumbers(data.numbers).map { _ =>
      data.numbers.foreach { x =>
        val file = new File(Utils.wildSampleADir, x)
        FileUtils.deleteQuietly(file)
      }
      Ok("success!")
    }
  }

  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    wildSampleDao.selectByNumberOp(data.number).map {
      case Some(y) => Ok(Json.obj("valid" -> false))
      case None => Ok(Json.obj("valid" -> true))
    }
  }

  def addSamplePhenotype = Action.async { implicit request =>
    val data = formTool.wildSampleForm.bindFromRequest().get
    val row = WildSampleRow(data.number, data.source, data.phenotype, data.comment, "")
    wildSampleDao.insert(row).map { x =>
      Ok("success!")
    }
  }

  def updateSamplePhenotypeBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.wildSample.updateSamplePhenotype(data.number))
  }

  def updateSamplePhenotype = Action.async { implicit request =>
    val data = formTool.wildSampleForm.bindFromRequest().get
    wildSampleDao.selectByNumber(data.number).flatMap { row =>
      val newRow = row.copy(data.number, data.source, data.phenotype, data.comment)
      wildSampleDao.update(newRow)
    }.map { x =>
      Ok("success!")
    }
  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.wildSample.detailInfo(data.number))
  }

  def manageAppendixBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.wildSample.manageAppendix(data.number))
  }

  def addAppendix = Action.async(parse.multipartFormData) { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val photoMap = request.body.files.map { file =>
      val finalFile = new File(Utils.wildSampleADir, data.number + File.separator + file.filename)
      FileUtils.writeStringToFile(finalFile, "")
      file.ref.moveTo(finalFile, true)
      (file.filename, "")
    }.toMap

    wildSampleDao.selectByNumber(data.number).flatMap { x =>
      val befPhoto = x.photo
      val map = Utils.str2Map(befPhoto)
      val newMap = map ++ photoMap
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      wildSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))

    }
  }

  def deleteAppendix = Action.async { implicit request =>
    val data = formTool.imageForm.bindFromRequest().get
    val file = new File(Utils.appendixDir, s"${data.dir}/${data.number}/${data.fileName}")
    FileUtils.deleteQuietly(file)
    wildSampleDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo)
      val newMap = map - data.fileName
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      wildSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def updateDesc = Action.async { implicit request =>
    val data = formTool.descForm.bindFromRequest().get
    wildSampleDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo) + (data.fileName -> data.describe)
      val newRow = x.copy(photo = Json.toJson(map).toString())
      wildSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }

  }


}
