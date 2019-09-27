package controllers

import java.io.File
import java.nio.file.Files

import dao.AccountDao
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.routing.JavaScriptReverseRouter
import tool.FormTool
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2019/1/11
  */
class AppController @Inject()(cc: ControllerComponents, accountDao: AccountDao,
                              formTool: FormTool) extends AbstractController(cc) {

  def loginBefore = Action { implicit request =>
    Ok(views.html.login())
  }

  def login = Action.async { implicit request =>
    val data = formTool.accountForm.bindFromRequest().get
    accountDao.selectById1.map { x =>
      if (data.account == x.account && data.password == x.password) {
        Redirect(routes.AdminController.toIndex()).withSession("admin" -> data.account)
      } else {
        Redirect(routes.AppController.loginBefore()).flashing("info" -> "账号或密码错误!")
      }
    }
  }

  def getImage = Action { implicit request =>
    val data = formTool.imageForm.bindFromRequest().get
    val ifModifiedSinceStr = request.headers.get(IF_MODIFIED_SINCE)
    val file = new File(Utils.appendixDir, data.dir+File.separator+data.number + File.separator + data.fileName)
    val lastModifiedStr = file.lastModified().toString
    val MimeType = "image/png"
    val byteArray = Files.readAllBytes(file.toPath)
    if (ifModifiedSinceStr.isDefined && ifModifiedSinceStr.get == lastModifiedStr) {
      NotModified
    } else {
      Ok(byteArray).as(MimeType).withHeaders(LAST_MODIFIED -> file.lastModified().toString)
    }
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        controllers.adminC.routes.javascript.LocalSampleController.numberCheck,
        controllers.adminC.routes.javascript.LocalSampleController.addSamplePhenotype,
        controllers.adminC.routes.javascript.LocalSampleController.updateSamplePhenotypeBefore,
        controllers.adminC.routes.javascript.LocalSampleController.updateSamplePhenotype,
        controllers.adminC.routes.javascript.LocalSampleController.deleteSamplePhenotypes,
        controllers.adminC.routes.javascript.LocalSampleController.deleteByNumber,
        controllers.adminC.routes.javascript.LocalSampleController.getDetailInfo,
        controllers.adminC.routes.javascript.LocalSampleController.addSamplePhenotypeByFile,
        controllers.adminC.routes.javascript.LocalSampleController.manageAppendixBefore,
        controllers.adminC.routes.javascript.LocalSampleController.deleteAppendix,
        controllers.adminC.routes.javascript.LocalSampleController.updateDesc,

        controllers.adminC.routes.javascript.BreedSampleController.addSamplePhenotypeByFile,
        controllers.adminC.routes.javascript.BreedSampleController.numberCheck,
        controllers.adminC.routes.javascript.BreedSampleController.addSamplePhenotype,
        controllers.adminC.routes.javascript.BreedSampleController.manageAppendixBefore,
        controllers.adminC.routes.javascript.BreedSampleController.updateDesc,
        controllers.adminC.routes.javascript.BreedSampleController.deleteAppendix,
        controllers.adminC.routes.javascript.BreedSampleController.deleteByNumber,
        controllers.adminC.routes.javascript.BreedSampleController.deleteSamplePhenotypes,
        controllers.adminC.routes.javascript.BreedSampleController.updateSamplePhenotypeBefore,
        controllers.adminC.routes.javascript.BreedSampleController.updateSamplePhenotype,
        controllers.adminC.routes.javascript.BreedSampleController.getDetailInfo,

        controllers.routes.javascript.LocalSampleController.getAllPhenotype,
        controllers.routes.javascript.LocalSampleController.getDetailInfo,
        controllers.routes.javascript.LocalSampleController.getSamplePhenotypeInfo,
        controllers.routes.javascript.LocalSampleController.getAllFiles,
        controllers.routes.javascript.LocalSampleController.getAllNumber,
        controllers.routes.javascript.LocalSampleController.getStatData,

        controllers.routes.javascript.BreedSampleController.getAllPhenotype,
        controllers.routes.javascript.BreedSampleController.getDetailInfo,
        controllers.routes.javascript.BreedSampleController.getSamplePhenotypeInfo,
        controllers.routes.javascript.BreedSampleController.getAllFiles,
        controllers.routes.javascript.BreedSampleController.getAllNumber,
        controllers.routes.javascript.BreedSampleController.getStatData,

        controllers.adminC.routes.javascript.WildSampleController.addSamplePhenotypeByFile,
        controllers.adminC.routes.javascript.WildSampleController.deleteByNumber,
        controllers.adminC.routes.javascript.WildSampleController.deleteSamplePhenotypes,
        controllers.adminC.routes.javascript.WildSampleController.numberCheck,
        controllers.adminC.routes.javascript.WildSampleController.addSamplePhenotype,
        controllers.adminC.routes.javascript.WildSampleController.updateSamplePhenotypeBefore,
        controllers.adminC.routes.javascript.WildSampleController.updateSamplePhenotype,
        controllers.adminC.routes.javascript.WildSampleController.getDetailInfo,
        controllers.adminC.routes.javascript.WildSampleController.manageAppendixBefore,
        controllers.adminC.routes.javascript.WildSampleController.deleteAppendix,
        controllers.adminC.routes.javascript.WildSampleController.updateDesc,

        controllers.routes.javascript.WildSampleController.getAllPhenotype,
        controllers.routes.javascript.WildSampleController.getSamplePhenotypeInfo,
        controllers.routes.javascript.WildSampleController.getAllFiles,
        controllers.routes.javascript.WildSampleController.getDetailInfo,
        controllers.routes.javascript.WildSampleController.getStatData,
        controllers.routes.javascript.WildSampleController.getAllNumber,

        controllers.adminC.routes.javascript.ExIntroductionController.addByFile,
        controllers.adminC.routes.javascript.ExIntroductionController.numberCheck,
        controllers.adminC.routes.javascript.ExIntroductionController.add,
        controllers.adminC.routes.javascript.ExIntroductionController.deleteByNumber,
        controllers.adminC.routes.javascript.ExIntroductionController.deletes,
        controllers.adminC.routes.javascript.ExIntroductionController.updateBefore,
        controllers.adminC.routes.javascript.ExIntroductionController.update,
        controllers.adminC.routes.javascript.ExIntroductionController.manageAppendixBefore,
        controllers.adminC.routes.javascript.ExIntroductionController.deleteAppendix,
        controllers.adminC.routes.javascript.ExIntroductionController.updateDesc,
        controllers.adminC.routes.javascript.ExIntroductionController.getDetailInfo,

        controllers.routes.javascript.ExIntroductionController.getAllPhenotype,
        controllers.routes.javascript.ExIntroductionController.getSamplePhenotypeInfo,
        controllers.routes.javascript.ExIntroductionController.getAllFiles,
        controllers.routes.javascript.ExIntroductionController.getDetailInfo,
        controllers.routes.javascript.ExIntroductionController.getAllNumber,
        controllers.routes.javascript.ExIntroductionController.getStatData,









        controllers.routes.javascript.AppController.getImage,

      )
    ).as("text/javascript")
  }

}
