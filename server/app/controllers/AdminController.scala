package controllers

import java.io.File
import java.nio.file.Files

import dao._
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import tool.FormTool
import models.Tables._
import utils.Utils

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global



/**
  * Created by yz on 2019/1/11
  */
class AdminController @Inject()(cc: ControllerComponents, formTool: FormTool,
                                accountDao: AccountDao) extends AbstractController(cc) {

  def toIndex = Action { implicit request =>
    Ok(views.html.admin.index())
  }

  def changePasswordBefore = Action { implicit request =>
    Ok(views.html.admin.changePassword())
  }

  def changePassword = Action.async { implicit request =>
    val data = formTool.changePasswordForm.bindFromRequest().get
    accountDao.selectById1.flatMap { x =>
      if (data.password == x.password) {
        val row = AccountRow(x.id, x.account, data.newPassword)
        accountDao.update(row).map { y =>
          Redirect(routes.AppController.loginBefore()).flashing("info" -> "密码修改成功!").withNewSession
        }
      } else {
        Future.successful(Redirect(routes.AdminController.changePasswordBefore()).flashing("info" -> "密码错误!"))
      }
    }
  }

  def logout = Action {implicit request=>
    Redirect(routes.AppController.loginBefore()).flashing("info" -> "退出登录成功!").withNewSession
  }

  def downloadExampleFile = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.path, s"example/${data.fileName}")
    Ok.sendFile(file).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> s"attachment; filename=${
        file.getName
      }",
      CONTENT_TYPE -> "application/x-download"
    )
  }



}
