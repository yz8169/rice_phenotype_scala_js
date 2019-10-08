package myJs.localSample

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.scalajs.js
import scala.scalajs.js.annotation._
import myJs.admin.localSample.Manage
import myJs.myPkg.Implicits._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("LocalSampleView")
object LocalSampleView {

  @JSExport("init")
  def init = {
    refreshTableView
    refreshTableData()

  }

  def refreshTableData(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.LocalSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      $("#table").bootstrapTable("load", data)
      f()
    }
    $.ajax(ajaxSettings)

  }

  def refreshTableView = {
    val phenotypeNames = Shared.localSamplePhenotypeNames
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
    g.$("#table").bootstrapTable()
    phenotypeNames.drop(7).foreach { x =>
      g.$("#table").bootstrapTable("hideColumn", x)
      $(s"input:checkbox[value='${x}']").attr("checked", false)
    }

  }

  @JSExport("numberFmt")
  def numberFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      val manageUrl = g.jsRoutes.controllers.LocalSampleController.getDetailInfo().url.toString
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
