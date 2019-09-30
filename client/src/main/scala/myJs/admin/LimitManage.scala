package myJs.admin

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool.{layerOptions, myElement}
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._
import org.scalajs.dom._
import org.scalajs.dom


import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
 * Created by Administrator on 2019/9/11
 */
@JSExportTopLevel("LimitManage")
object LimitManage {

  var lastChecked: Element = null
  var myData: js.Array[js.Dictionary[String]] = _
  var localSampleNumbers: js.Array[String] = _

  @JSExport("init")
  def init = {
    $("#table").bootstrapTable()
    refreshUserLimit()
    bootStrapValidator
    productLocalCheckbox
    //    show("10")

  }

  def fillCheckbox = {
    val html = localSampleNumbers.map { inName =>
      label(marginRight := 15,
        input(`type` := "checkbox", cls := "ckb", name := "numbers[]", value := inName, onclick := s"Utils.setColumns('${inName}')", inName)
      )
    }.mkString
    $("#checkbox").html(html)
  }

  def shiftCheckbox = {
    val checkboxs = $("input:checkbox")
    checkboxs.on("click", (y: Element, e: Event) => {
      val me = e.asInstanceOf[MouseEvent]
      if (lastChecked == null) {
        lastChecked = y
      } else {
        if (me.shiftKey) {
          val start = checkboxs.index(y)
          val end = checkboxs.index(lastChecked)
          val status = $(lastChecked).is(":checked")
          checkboxs.slice(math.min(start, end), math.max(start, end) + 1).prop("checked", status)
        }
        lastChecked = y
      }
    })

  }

  def productLocalCheckbox = {
    val url = g.jsRoutes.controllers.LocalSampleController.getAllNumber().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[String]]
      localSampleNumbers = rs
      fillCheckbox
    }
    $.ajax(ajaxSettings)
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
        refreshUserLimit { () =>
          Swal.swal(SwalOptions.title("成功").text("删除成功").`type`("success"))
        }
      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("删除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)

    })
  }

  @JSExport("refreshUser")
  def refreshUserLimit(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.AdminController.getAllUserLimit().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      $("#table").bootstrapTable("load", data)
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      myData = rs
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("operateFmt")
  def operateFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val url = g.jsRoutes.controllers.AdminController.limitUpdateBefore().url.toString
      val updateStr = a(
        title := "修改",
        cursor.pointer,
        href:=s"${url}?id=${row("id")}",
//        onclick := s"LimitManage.show('" + row("id") + "')",
        target := "_blank",
        span(
          em(cls := "fa fa-edit")
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
      Array(updateStr, deleteStr).mkString("&nbsp;")
  }

  @JSExport("show")
  def show(id: String) = {
    val index = layer.alert(myElement, layerOptions)
    val inDict = myData.filter { dict =>
      dict("id") == id
    }.head
    $(":input[name='id']").`val`(id)
    $("#name").text(inDict("name"))
    $("input:checkbox").prop("checked",false)
    val selectedNumbers = inDict("localSample").split(";")
    selectedNumbers.foreach { number =>
      $(s":input[value=${number}]").prop("checked", true)
    }
    layer.close(index)
    jQuery("#modal").modal("show")
    shiftCheckbox
  }

  @JSExport("updateLimit")
  def updateLimit = {
    val index = layer.alert(myElement, layerOptions)
    val url = g.jsRoutes.controllers.AdminController.refreshLimit().url.toString
    val data = $(s"#form").serialize()
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(data).success { (data, status, e) =>
      refreshUserLimit { () =>
        layer.close(index)
        jQuery("#modal").modal("hide")
        Swal.swal(SwalOptions.title("成功").text("用户权限更新成功!").`type`("success"))
      }

    }
    $.ajax(ajaxSettings)

  }


  def refreshLocalSample = {


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
        refreshUserLimit { () =>
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
        refreshUserLimit { () =>
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
