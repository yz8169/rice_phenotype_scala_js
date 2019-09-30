package myJs.admin

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool.{layerOptions, myElement}
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery.{$, JQueryAjaxSettings}
import org.scalajs.dom._
import org.scalajs.jquery.JQueryEventObject
import scalatags.Text.all._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._


/**
 * Created by Administrator on 2019/9/11
 */
@JSExportTopLevel("LimitUpdate")
object LimitUpdate {

  val ids = ArrayBuffer[String]()

  var lastChecked: Element = null
  var myData: js.Array[js.Dictionary[String]] = _
  var localSampleNumbers: js.Array[String] = _
  var localSamples: js.Array[js.Dictionary[String]] = _
  var selectedNumbers: Set[String] = _
  var isShifit: Boolean = false

  @JSExport("init")
  def init = {
    initTable
    //    $("#table").bootstrapTable()
    //    bootStrapValidator
    //    productLocalCheckbox
    refreshUserLimit { () =>
      refreshTableData { () =>
        bindEvt
      }
    }

    //    show("10")

  }

  def initTable = {
    val checkColumn = ColumnOptions.field("checked").checkbox(true).formatter(checkFmt)
    val columnNames = js.Array("number", "name", "unitNumber", "comment")
    val columns = js.Array(checkColumn) ++ columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "统一编号"
        case "name" => "品种名称"
        case "unitNumber" => "单位编号"
        case "comment" => "备注"
        case _ => columnName
      }
      val fmt = tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).sortable(true).formatter(fmt)
    }

    val options = TableOptions.columns(columns)
    $("#table").bootstrapTable(options)
  }

  def getIds = {
    ids.clear()
    val arrays = g.$("#table").bootstrapTable("getSelections").asInstanceOf[js.Array[js.Dictionary[String]]]
    ids ++= arrays.map { x =>
      x("number")
    }
    if (ids.isEmpty) $("#deleteButton").attr("disabled", true) else
      $("#deleteButton").attr("disabled", false)

  }

  val refreshIds = (e: JQueryEventObject, row: js.Dictionary[js.Any], y: Element) => {
    shiftCheckbox(y)
    getIds
    updateLimit
  }

  val refreshIdNoShift = (e: JQueryEventObject, row: js.Dictionary[js.Any], y: Element) => {
    getIds
    updateLimit
  }


  def refreshUserLimit(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.AdminController.getAllUserLimit().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      myData = rs

      f()
    }
    $.ajax(ajaxSettings)

  }

  def bindEvt = {
    org.scalajs.jquery.jQuery("#table").on("check.bs.table", refreshIdNoShift)
    $("tr[data-index] > td").on("mousedown", (y: Element, e: Event) => {
      val me = e.asInstanceOf[MouseEvent]
      isShifit = me.shiftKey
      shiftCheckbox(y)
    })

    org.scalajs.jquery.jQuery("#table").on("uncheck.bs.table", refreshIdNoShift)
    org.scalajs.jquery.jQuery("#table").on("check-all.bs.table", refreshIdNoShift)
    org.scalajs.jquery.jQuery("#table").on("uncheck-all.bs.table", refreshIdNoShift)
  }

  def numberA(v: String) = {
    val manageUrl = g.jsRoutes.controllers.adminC.LocalSampleController.getDetailInfo().url.toString
    val manageStr = a(
      title := "详细信息",
      cursor.pointer,
      href := s"${manageUrl}?number=${v}",
      target := "_blank",
      v
    )
    Array(manageStr).mkString("&nbsp;")
  }

  def tbFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[Any]) => columnName match {
    case "number" => {
      numberA(v.toString)
    }
    case x => {
      v
    }
  }

  def checkFmt: js.Function = (v: js.Any, row: js.Dictionary[Any]) => {
    val number = row("number").toString
    if (selectedNumbers.contains(number)) {
      true
    } else false

  }

  def refreshTableData(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.LocalSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      val inDict = myData.filter { dict =>
        dict("id") == "10"
      }.head
      selectedNumbers = inDict("localSample").split(";").toSet
      //      val newRs = rs.map { dict =>
      //        val number = dict("number")
      //        if (selectedNumbers.contains(number)) {
      //          val map = dict ++ Map("checked" -> "true")
      //          map.toJSDictionary
      //        } else dict
      //      }
      //      println(newRs.head)
      localSamples = rs
      $("#table").bootstrapTable("load", rs)
      shiftCheckbox
      f()
    }
    $.ajax(ajaxSettings)

  }

  def fillCheckbox = {
    val html = localSampleNumbers.map { inName =>
      label(marginRight := 15,
        input(`type` := "checkbox", cls := "ckb", name := "numbers[]", value := inName, onclick := s"Utils.setColumns('${inName}')", inName)
      )
    }.mkString
    $("#checkbox").html(html)
  }

  def shiftCheckbox = (y: Element) => {
    println($(y).prop("outerHTML"))
    val data = $("#table").bootstrapTable("getData")
    if (lastChecked == null) {
      lastChecked = y
    } else {
      if (isShifit) {
        val start = $(y).find(">input").attr("data-index").toString.toInt
        val end = $(lastChecked).find(">input").attr("data-index").toString.toInt
        println($(lastChecked).parent().html())
        val status = $(lastChecked).parent().hasClass("selected")
        println(status)
        val min = math.min(start, end)
        val max = math.max(start, end)
        (min until max).foreach { i =>
          if (status) {
            $("#table").bootstrapTable("check", i)
          } else {
            $("#table").bootstrapTable("uncheck", i)
          }

        }
      }
      lastChecked = y
    }

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

  @JSExport("operateFmt")
  def operateFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val url = g.jsRoutes.controllers.AdminController.limitUpdateBefore().url.toString
      val updateStr = a(
        title := "修改",
        cursor.pointer,
        href := s"${url}?id=${row("id")}",
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
    $("input:checkbox").prop("checked", false)
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
    val url = g.jsRoutes.controllers.AdminController.refreshLimit().url.toString
    println(ids)
    val data = js.Dictionary(
      "id" -> 10,
      "numbers" -> ids.toJSArray
    )
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      selectedNumbers = selectedNumbers ++ ids.toSet
      //      refreshTableData { () =>
      //      }

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
