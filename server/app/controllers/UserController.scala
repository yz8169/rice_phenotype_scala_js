package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import utils.Utils

/**
 * Created by Administrator on 2019/9/27
 */
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def logout = Action { implicit request =>
    Redirect(routes.AppController.loginBefore()).flashing("info" -> "退出登录成功!", "class" -> Utils.successClass).
      removingFromSession(Shared.userStr)
  }


}
