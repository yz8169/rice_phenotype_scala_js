package myJs.breedSample

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import myJs.admin.breedSample.Manage


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("BreedSampleView")
object BreedSampleView {


  @JSExport("init")
  def init = {
    refreshTableView
    Manage.refreshTableData()

  }

  def refreshTableView = {
    val phenotypeNames = Shared.breedSamplePhenotypeNames
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
      val manageUrl = g.jsRoutes.controllers.BreedSampleController.getDetailInfo().url.toString
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
