package myJs.admin.wildSample

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import myJs.myPkg.Implicits._
import myJs.myPkg._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("WildSampleManage")
object WildSampleManage {

  val ids = ArrayBuffer[String]()

  @JSExport("init")
  def init = {
    refreshTableView
    refreshTableData()

  }

  def refreshTableView = {
    val phenotypeNames = Shared.wildSamplePhenotypeNames
    val html = phenotypeNames.map { x =>
      label(marginRight := 15,
        input(`type` := "checkbox", checked, value := x, onclick := s"Utils.setColumns('${x}')", x)
      )
    }.mkString
    $("#checkbox").html(html)
    val thHtml = phenotypeNames.map { x =>
      th(dataField := x, dataSortable := true, x)
    }.mkString
    $("#marker").after(thHtml)
    $("#table").bootstrapTable()
    phenotypeNames.drop(6).foreach { x =>
      $("#table").bootstrapTable("hideColumn", x)
      $(s"input:checkbox[value='${x}']").attr("checked", false)
    }
    bindEvt

  }

  def bindEvt = {
    $("#table").on("check.bs.table", () => getIds)
    $("#table").on("uncheck.bs.table", () => getIds)
    $("#table").on("check-all.bs.table", () => getIds)
    $("#table").on("uncheck-all.bs.table", () => getIds)
    $("#table").on("page-change.bs.table", () => getIds)
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

  def refreshTableData(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.adminC.WildSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      $("#table").bootstrapTable("load", data)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("deletes")
  def deletes = {
    val dict = js.Dictionary(
      "title" -> "",
      "text" -> "确定要删除选中的数据吗？",
      "type" -> "warning",
      "showCancelButton" -> true,
      "showConfirmButton" -> true,
      "confirmButtonClass" -> "btn-danger",
      "confirmButtonText" -> "确定",
      "closeOnConfirm" -> false,
      "cancelButtonText" -> "取消",
    )
    g.swal(dict, () => {
      val data = js.Dictionary(
        "numbers" -> ids.toJSArray
      )
      val url = g.jsRoutes.controllers.adminC.WildSampleController.deleteSamplePhenotypes().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
        `type`("post").data(JSON.stringify(data)).success { (data, status, e) =>
        refreshTableData{()=>
          Swal.swal(SwalOptions.title("成功").text("删除数据成功!").`type`("success"))
        }
        getIds
      }.error { (data, status, e) =>
        g.swal("错误", "删除失败！", "error")
      }
      $.ajax(ajaxSettings)
    })

  }

  @JSExport("operateFmt")
  def operateFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val deleteStr = a(
        title := "删除",
        onclick := s"WildSampleManage.delete('${row("number")}')",
        cursor.pointer,
        span(
          em(cls := "fa fa-close")
        )
      )
      val updateUrl = g.jsRoutes.controllers.adminC.WildSampleController.updateSamplePhenotypeBefore().url.toString
      val updateStr = a(
        title := "修改",
        cursor.pointer,
        href := s"${updateUrl}?number=${row("number")}",
        target := "_blank",
        span(
          em(cls := "fa fa-edit")
        )
      )
      Array(deleteStr, updateStr).mkString("&nbsp;")

  }

  @JSExport("delete")
  def delete(number: String) = {
    val dict = js.Dictionary(
      "title" -> "",
      "text" -> "确定要删除此数据吗？",
      "type" -> "warning",
      "showCancelButton" -> true,
      "showConfirmButton" -> true,
      "confirmButtonClass" -> "btn-danger",
      "confirmButtonText" -> "确定",
      "closeOnConfirm" -> false,
      "cancelButtonText" -> "取消",
    )
    g.swal(dict, () => {
      val url = g.jsRoutes.controllers.adminC.WildSampleController.deleteByNumber().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshTableData{()=>
          Swal.swal(SwalOptions.title("成功").text("删除成功!").`type`("success"))
        }

      }.error { (data, status, e) =>
        g.swal("错误", "删除失败！", "error")
      }
      $.ajax(ajaxSettings)
    })

  }

  @JSExport("aOperateFmt")
  def aOperateFmt: js.Function = {
    (v: js.Any, row: js.Dictionary[js.Any]) =>
      val manageUrl = g.jsRoutes.controllers.adminC.WildSampleController.manageAppendixBefore().url.toString
      val manageStr = a(
        title := "管理",
        cursor.pointer,
        href := s"${manageUrl}?number=${row("number")}",
        target := "_blank",
        span(
          em(cls := "fa fa-user")
        )
      )
      Array(manageStr).mkString("&nbsp;")

  }

  @JSExport("numberFmt")
  def numberFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      val manageUrl = g.jsRoutes.controllers.adminC.WildSampleController.getDetailInfo().url.toString
      val manageStr = a(
        title := "详细信息",
        cursor.pointer,
        href := s"${manageUrl}?number=${v}",
        target := "_blank",
        v
      )
      Array(manageStr).mkString("&nbsp;")

  }


}
