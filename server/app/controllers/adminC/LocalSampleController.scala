package controllers.adminC

import java.io.File

import controllers.adminC
import dao.LocalSampleDao
import javax.inject.Inject
import models.Tables.LocalSampleRow
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import play.api.mvc._
import shared.Shared
import tool._
import utils.Utils

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by yz on 2019/3/6
  */
class LocalSampleController @Inject()(cc: ControllerComponents, localSampleDao: LocalSampleDao,
                                      formTool: FormTool,tool:Tool) extends AbstractController(cc) {

  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    localSampleDao.selectByNumberOp(data.number).map {
      case Some(y) => Ok(Json.obj("valid" -> false))
      case None => Ok(Json.obj("valid" -> true))
    }
  }

  def addSamplePhenotype = Action.async { implicit request =>
    val data = formTool.localSampleForm.bindFromRequest().get
    val row = LocalSampleRow(data.number, data.name, data.unitNumber, data.phenotype, data.comment, "")
    localSampleDao.insert(row).map { x =>
      Ok("success!")
    }
  }

  def updateSamplePhenotype = Action.async { implicit request =>
    val data = formTool.localSampleForm.bindFromRequest().get
    localSampleDao.selectByNumber(data.number).flatMap { row =>
      val newRow = row.copy(data.number, data.name, data.unitNumber, data.phenotype, data.comment)
      localSampleDao.update(newRow)
    }.map { x =>
      Ok("success!")
    }
  }

  def updateSamplePhenotypeBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.localSample.updateSamplePhenotype(data.number))
  }

  def updateDesc = Action.async { implicit request =>
    val data = formTool.descForm.bindFromRequest().get
    localSampleDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo) + (data.fileName -> data.describe)
      val newRow = x.copy(photo = Json.toJson(map).toString())
      localSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }

  }

  def deleteAppendix = Action.async { implicit request =>
    val data = formTool.imageForm.bindFromRequest().get
    val file = new File(Utils.appendixDir, s"${data.dir}/${data.number}/${data.fileName}")
    FileUtils.deleteQuietly(file)
    localSampleDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo)
      val newMap = map - data.fileName
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      localSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def addAppendix = Action.async(parse.multipartFormData) { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val photoMap = request.body.files.map { file =>
      val finalFile = new File(Utils.localSampleADir, data.number + File.separator + file.filename)
      FileUtils.writeStringToFile(finalFile, "")
      file.ref.moveTo(finalFile, true)
      (file.filename, "")
    }.toMap

    localSampleDao.selectByNumber(data.number).flatMap { x =>
      val befPhoto = x.photo
      val map = Utils.str2Map(befPhoto)
      val newMap = map ++ photoMap
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      localSampleDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))

    }
  }

  def manageAppendixBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.localSample.manageAppendix(data.number))
  }

  def deleteSamplePhenotypes = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    localSampleDao.deleteByNumbers(data.numbers).map { _ =>
      data.numbers.foreach { x =>
        val file = new File(Utils.localSampleADir, x)
        FileUtils.deleteQuietly(file)
      }
      Ok("success")
    }
  }

  def deleteByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    localSampleDao.deleteByNumber(data.number).map { x =>
      val appendixDir = new File(Utils.localSampleADir, data.number)
      Utils.deleteDirectory(appendixDir)
      Ok("success")
    }
  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.localSample.detailInfo(data.number))
  }

  def addSamplePhenotypeByFileBefore = Action { implicit request =>
    Ok(views.html.admin.localSample.addSamplePhenotypeByFile())
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
      val phenotypeNames = Shared.localSamplePhenotypeNames
      val lines = Utils.xlsx2Lines(tmpXlsxFile)
      val headers = lines.head.split("\t")
      val rows = lines.drop(1).map { line =>
        val columns = line.split("\t")
        val map = headers.zip(columns).toMap
        val phenotypeMap = map.filter { case (key, value) =>
          phenotypeNames.contains(key)
        }
        LocalSampleRow(map("统一编号"), map("品种名称"), map("单位编号"), Json.toJson(phenotypeMap).toString(),
          map.getOrElse("备注", ""), "")
      }

      localSampleDao.insertOrUpdates(rows).map { x =>
        tool.deleteDirectory(tmpDir)
        Ok("success!")
      }
    }
  }

  def checkFile(file: File) = {
    var error = ""
    (true, error)
  }

  def addSamplePhenotypeBefore = Action { implicit request =>
    Ok(views.html.admin.localSample.addSamplePhenotype())
  }

  def manageSamplePhenotypeBefore = Action { implicit request =>
    Ok(views.html.admin.localSample.manageSamplePhenotype())
  }


}
