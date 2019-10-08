package controllers

import java.io.File
import java.nio.file.Files

import dao._
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import tool._
import models.Tables._
import org.joda.time.DateTime
import play.api.libs.json.Json
import utils.Utils

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by yz on 2019/1/11
 */
class AdminController @Inject()(cc: ControllerComponents, formTool: FormTool,
                                accountDao: AccountDao, val userDao: UserDao,
                                val userLimitDao: UserLimitDao) extends AbstractController(cc) with
  AdminTool with AdminToolWithLimit {

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

  def logout = Action { implicit request =>
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

  def userManageBefore = Action { implicit request =>
    Ok(views.html.admin.userManage())
  }

  def getAllUser = Action.async { implicit request =>
    userDao.selectAll.map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def userNameCheck = Action.async { implicit request =>
    val data = formTool.userNameForm.bindFromRequest.get
    userDao.selectByName(data.name).zip(accountDao.selectById1).map { case (optionUser, admin) =>
      optionUser match {
        case Some(y) => Ok(Json.obj("valid" -> false))
        case None =>
          val valid = if (data.name == admin.account) false else true
          Ok(Json.obj("valid" -> valid))
      }
    }
  }

  def addUser = Action.async { implicit request =>
    val data = formTool.userForm.bindFromRequest().get
    val row = UserRow(0, data.name, data.password, new DateTime())
    insertUser(row).map { userId =>
      Ok(Json.toJson("success!"))
    }
  }

  def deleteUserById = Action.async { implicit request =>
    val data = formTool.idForm.bindFromRequest().get
    deleteById(data.id).map { x =>
      Ok("success")
    }
  }

  def getUserById = Action.async { implicit request =>
    val data = formTool.idForm.bindFromRequest().get
    userDao.selectById(data.id).map { x =>
      Ok(Utils.getJsonByT(x))
    }
  }

  def updateUser = Action.async { implicit request =>
    val data = formTool.userForm.bindFromRequest().get
    userDao.update(data).map { x =>
      Ok("success")
    }

  }

  def limitManageBefore = Action { implicit request =>
    Ok(views.html.admin.limitManage())
  }

  def getAllUserLimit = Action.async { implicit request =>
    userDao.selectAll.zip(userLimitDao.selectAll).map { case (users, userLimits) =>
      val ts = users.zip(userLimits)
      val array = Utils.getArrayByTs(ts)
      Ok(Json.toJson(array))
    }
  }

  def refreshLimit = Action.async { implicit request =>
    val data = formTool.userLimitForm.bindFromRequest().get
    val row = UserLimitRow(data.id, data.localNumbers.mkString(";"), data.breedNumbers.mkString(";"),
      data.wildNumbers.mkString(";"), data.exIntroductionNumbers.mkString(";"))
    userLimitDao.update(row).map { x =>
      Ok(Json.toJson("success"))
    }
  }


  def limitUpdateBefore = Action { implicit request =>
    val data = formTool.idOpForm.bindFromRequest().get
    Ok(views.html.admin.limitUpdate(data.id))
  }

  def getUserLimitById = Action.async { implicit request =>
    val data = formTool.idForm.bindFromRequest().get
    userDao.selectById(data.id).zip(userLimitDao.selectById(data.id)).map { case (user, userLimit) =>
      val t = (user, userLimit)
      val map = Utils.getMapByT(t)
      Ok(Json.toJson(map))
    }
  }

  def getAllUserNames = Action.async { implicit request =>
    userDao.selectAll.map { users =>
      val array = users.map { user =>
        Map("id" -> user.id.toString, "text" -> user.name)
      }
      Ok(Json.toJson(array))
    }
  }


}
