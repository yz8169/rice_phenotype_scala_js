package myJs.exIntroduction

import myJs.Utils._
import myJs.admin.exIntroduction.ExIntroductionManage
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation._
import myJs.myPkg.Implicits._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("ExIntroductionView")
object ExIntroductionView {


  @JSExport("init")
  def init = {
    refreshTableView
    refreshTableData()

  }

  def refreshTableData(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.ExIntroductionController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      $("#table").bootstrapTable("load", data)
      f()
    }
    $.ajax(ajaxSettings)

  }


  def refreshTableView = {
    val phenotypeNames = Shared.exIntroductionPhenotypeNames
    val hideMap=mutable.LinkedHashMap("库编号"->"warehouseNumber","单位编号"->"unitNumber","品种原名"->"oldName",
      "科名"->"family","属名"->"genus","学名"->"scientificName","保存单位"->"storeUnit","原号引号"->"originalNumber",
      "保存单位_2"->"storeUnit2","省"->"province","样品类型"->"sampleKind"
    )
    val nameMap=hideMap++phenotypeNames.map{ x=>
      (x->x)
    }
    val html = nameMap.map {case(name,field) =>
      label(marginRight := 15,
        input(`type` := "checkbox", checked, value := field, onclick := s"Utils.setColumns('${field}')", name)
      )
    }.mkString
    $("#checkbox").html(html)
    val thHtml = phenotypeNames.map { x =>
      th(dataField := x, dataSortable := true, x)
    }.mkString
    $("#marker").after(thHtml)
    $("#table").bootstrapTable()
    val hideColumns=hideMap.values.toList++phenotypeNames.drop(7)
    hideColumns.foreach { x =>
      $("#table").bootstrapTable("hideColumn", x)
      $(s"input:checkbox[value='${x}']").attr("checked", false)
    }

  }

  @JSExport("numberFmt")
  def numberFmt: js.Function = {
    (v: String, row: js.Dictionary[js.Any]) =>
      val manageUrl = g.jsRoutes.controllers.ExIntroductionController.getDetailInfo().url.toString
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
