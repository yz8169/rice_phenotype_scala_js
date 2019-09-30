package myJs.admin

import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{Date, JSON}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool.{layerOptions, myElement}
import scalatags.Text.all._


/**
 * Created by Administrator on 2019/9/11
 */
@JSExportTopLevel("UserManage")
object UserManage {

  @JSExport("init")
  def init = {
    $("#table").bootstrapTable()
    refreshUser()
    bootStrapValidator

  }

  @JSExport("deleteData")
  def deleteData(id: String) = {
    val options = SwalOptions.title("").text("确定要删除此数据吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消").showLoaderOnConfirm(true)
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.AdminController.deleteUserById().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?id=${id}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshUser { () =>
          Swal.swal(SwalOptions.title("成功").text("删除成功").`type`("success"))
        }
      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("删除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)

    })
  }

  @JSExport("refreshUser")
  def refreshUser(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.AdminController.getAllUser().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      $("#table").bootstrapTable("load", data)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("operateFmt")
  def operateFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val resetStr = a(
        title := "重置密码",
        onclick := s"UserManage.resetShow('" + row("id") + "')",
        cursor.pointer,
        span(
          em(cls := "fa fa-repeat")
        )
      )
      val deleteStr = a(
        title := "删除",
        cursor.pointer,
        onclick := s"UserManage.deleteData('" + row("id") + "')",
        target := "_blank",
        span(
          em(cls := "fa fa-close")
        )
      )
      Array(resetStr, deleteStr).mkString("&nbsp;")
  }

  @JSExport("addShow")
  def addShow = {
    jQuery("#addModal").modal("show")

  }

  @JSExport("add")
  def add = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(s"#form").serialize()
      val index = layer.alert(myElement, layerOptions)
      val url = g.jsRoutes.controllers.AdminController.addUser().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(data).success { (data, status, e) =>
        refreshUser { () =>
          layer.close(index)
          jQuery("#addModal").modal("hide")
          bv.resetForm(true)
          Swal.swal(SwalOptions.title("成功").text("新增成功!").`type`("success"))
        }

      }
      $.ajax(ajaxSettings)

    }

  }

  @JSExport("resetShow")
  def resetShow(id: String) = {
    val url = g.jsRoutes.controllers.AdminController.getUserById().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?id=${id}").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[String]]
      $("#updateForm input[name='name']").`val`(rs("name"))
      jQuery("#updateModal").modal("show")
    }
    $.ajax(ajaxSettings)
  }

  @JSExport("update")
  def update = {
    val formId = "updateForm"
    val bv = jQuery(s"#${formId}").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(s"#${formId}").serialize()
      val index = layer.alert(myElement, layerOptions)
      val url = g.jsRoutes.controllers.AdminController.updateUser().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(data).success { (data, status, e) =>
        refreshUser { () =>
          layer.close(index)
          jQuery("#updateModal").modal("hide")
          bv.resetForm(true)
          Swal.swal(SwalOptions.title("成功").text("密码重置成功!").`type`("success"))
        }

      }
      $.ajax(ajaxSettings)
    }
  }

  def bootStrapValidator = {
    val url = g.jsRoutes.controllers.AdminController.userNameCheck().url.toString
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
        "name" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "用户名不能为空！"
            ),
            "remote" -> js.Dictionary(
              "message" -> "用户名已存在！",
              "url" -> url,
              "delay" -> 1000,
              "type" -> "POST",
            ),
          )
        ),
        "password" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "密码不能为空！"
            ),
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)
    updateFormBootStrapValidator

  }

  def updateFormBootStrapValidator = {
    val url = g.jsRoutes.controllers.AdminController.userNameCheck().url.toString
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
        "password" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "密码不能为空！"
            ),
          )
        ),
      )
    )
    g.$("#updateForm").bootstrapValidator(dict)

  }


}
