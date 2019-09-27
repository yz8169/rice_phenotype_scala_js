package controllers.adminC

import java.io.File

import dao._
import javax.inject.Inject
import models.Tables._
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import tool.{FormTool, Tool}
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2019/5/5
  */
class ExIntroductionController @Inject()(cc: ControllerComponents, exIntroductionDao: ExIntroductionDao,
                                         formTool: FormTool, tool: Tool) extends
  AbstractController(cc) {

  def addBefore = Action { implicit request =>
    Ok(views.html.admin.exIntroduction.add())
  }

  def addByFileBefore = Action { implicit request =>
    Ok(views.html.admin.exIntroduction.addByFile())
  }

  def addByFile = Action.async(parse.multipartFormData) { request =>
    val file = request.body.file("file").get
    val tmpDir = tool.createTempDirectory("tmpDir")
    val tmpXlsxFile = new File(tmpDir, "tmp.xlsx")
    file.ref.moveTo(tmpXlsxFile, true)
    val bm = checkFile(tmpXlsxFile)
    if (!bm._1) {
      tool.deleteDirectory(tmpDir)
      Future.successful(Ok(Json.obj("error" -> bm._2)))
    } else {
      val phenotypeNames = Shared.exIntroductionPhenotypeNames
      val lines = Utils.xlsx2Lines(tmpXlsxFile)
      val headers = lines.head.split("\t")
      val rows = lines.drop(1).map { line =>
        val columns = line.split("\t")
        headers.zip(columns).toMap
      }.filter { map =>
        val id = map("统一编号")
        StringUtils.isNotEmpty(id)
      }.groupBy { map =>
        map("统一编号")
      }.mapValues { maps =>
        maps.last
      }.map { case (id, map) =>
        map
      }.toList.map { map =>
        val phenotypeMap = map.filter { case (key, value) =>
          phenotypeNames.contains(key)
        }
        ExIntroductionRow(number = map("统一编号"), warehouseNumber = map("库编号"), unitNumber = map("单位编号"),
          name = map("品种名称"), oldName = map("品种原名"), family = map("科名"), genus = map("属名"),
          scientificName = map("学名"), sourceArea = map("原产地"), seedSource = map("种子来源"),
          storeUnit = map("保存单位"), originalNumber = map("原号引号"), phenotype = Json.toJson(phenotypeMap).toString(),
          storeUnit2 = map("保存单位_2"), province = map("省"), sampleKind = map("样品类型"),
          comment = map.getOrElse("备注", ""), photo = ""
        )
      }

      exIntroductionDao.insertOrUpdates(rows).map { x =>
        tool.deleteDirectory(tmpDir)
        Ok("success!")
      }
    }
  }

  def checkFile(file: File) = {
    var error = ""
    (true, error)
  }

  def manageBefore = Action { implicit request =>
    Ok(views.html.admin.exIntroduction.manage())
  }

  def deleteByNumber = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    exIntroductionDao.deleteByNumber(data.number).map { x =>
      val appendixDir = new File(Utils.exIntroductionADir, data.number)
      Utils.deleteDirectory(appendixDir)
      Ok("success!")
    }
  }

  def deletes = Action.async { implicit request =>
    val data = formTool.numbersForm.bindFromRequest().get
    exIntroductionDao.deleteByNumbers(data.numbers).map { _ =>
      data.numbers.foreach { x =>
        val file = new File(Utils.exIntroductionADir, x)
        FileUtils.deleteQuietly(file)
      }
      Ok("success!")
    }
  }

  def numberCheck = Action.async { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    exIntroductionDao.selectByNumberOp(data.number).map {
      case Some(y) => Ok(Json.obj("valid" -> false))
      case None => Ok(Json.obj("valid" -> true))
    }
  }

  def add = Action.async { implicit request =>
    val data = formTool.exIntroductionForm.bindFromRequest().get
    val row = ExIntroductionRow(data.number,data.warehouseNumber,data.unitNumber,data.name,data.oldName,data.family,
      data.genus,data.scientificName,data.sourceArea,data.seedSource,data.storeUnit,data.originalNumber,data.storeUnit2,
      data.province,data.sampleKind,data.phenotype,data.comment,"")
    exIntroductionDao.insert(row).map { x =>
      Ok("success!")
    }
  }

  def updateBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.exIntroduction.update(data.number))
  }

  def update = Action.async { implicit request =>
    val data = formTool.exIntroductionForm.bindFromRequest().get
    exIntroductionDao.selectByNumber(data.number).flatMap { row =>
      val newRow = row.copy(data.number,data.warehouseNumber,data.unitNumber,data.name,data.oldName,data.family,
        data.genus,data.scientificName,data.sourceArea,data.seedSource,data.storeUnit,data.originalNumber,data.storeUnit2,
        data.province,data.sampleKind,data.phenotype,data.comment)
      exIntroductionDao.update(newRow)
    }.map { x =>
      Ok("success!")
    }
  }

  def getDetailInfo = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.exIntroduction.detailInfo(data.number))
  }

  def manageAppendixBefore = Action { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    Ok(views.html.admin.exIntroduction.manageAppendix(data.number))
  }

  def addAppendix = Action.async(parse.multipartFormData) { implicit request =>
    val data = formTool.numberForm.bindFromRequest().get
    val photoMap = request.body.files.map { file =>
      val finalFile = new File(Utils.exIntroductionADir, data.number + File.separator + file.filename)
      println(finalFile)
      FileUtils.writeStringToFile(finalFile, "")
      file.ref.moveTo(finalFile, true)
      (file.filename, "")
    }.toMap

    exIntroductionDao.selectByNumber(data.number).flatMap { x =>
      val befPhoto = x.photo
      val map = Utils.str2Map(befPhoto)
      val newMap = map ++ photoMap
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      exIntroductionDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))

    }
  }

  def deleteAppendix = Action.async { implicit request =>
    val data = formTool.imageForm.bindFromRequest().get
    val file = new File(Utils.appendixDir, s"${data.dir}/${data.number}/${data.fileName}")
    FileUtils.deleteQuietly(file)
    exIntroductionDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo)
      val newMap = map - data.fileName
      val newRow = x.copy(photo = Json.toJson(newMap).toString())
      exIntroductionDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def updateDesc = Action.async { implicit request =>
    val data = formTool.descForm.bindFromRequest().get
    exIntroductionDao.selectByNumber(data.number).flatMap { x =>
      val map = Utils.str2Map(x.photo) + (data.fileName -> data.describe)
      val newRow = x.copy(photo = Json.toJson(map).toString())
      exIntroductionDao.update(newRow)
    }.map { x =>
      Ok(Json.toJson("success"))
    }

  }


}
