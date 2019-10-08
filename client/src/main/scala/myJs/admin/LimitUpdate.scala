package myJs.admin

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool
import myJs.Tool.{layerOptions, myElement}
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery.{$, JQueryAjaxSettings, JQueryEventObject}
import org.scalajs.dom._
//import org.scalajs.jquery.JQueryEventObject
import scalatags.Text.all._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._
import org.scalajs.jquery.{jQuery => nativeJq}
import myJs.Pojo._


/**
 * Created by Administrator on 2019/9/11
 */
@JSExportTopLevel("LimitUpdate")
object LimitUpdate {

  var isShifit: Boolean = false
  var idStr: String = _

  @JSExport("init")
  def init = {
    initTable
    refreshNames()
  }

  def refreshTable = {
    refreshUserLimit { () =>
      refreshTableData { () =>
        bindEvt(LocalTable)
      }
      refreshBreedTableData { () =>
        bindEvt(BreedTable)
      }
      refreshWildTableData { () =>
        bindEvt(WildTable)
      }
      refreshExIntroductionTableData { () =>
        bindEvt(ExIntroductionTable)
      }

    }
  }

  @JSExport("userChange")
  def userChange(y: Element) = {
    val id = $(y).find(">option:selected").`val`().toString
    idStr = id
    refreshTable

  }

  def refreshNames(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.AdminController.getAllUserNames().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[String]]
      $(":input[name='name']").select2(Select2Options.data(rs))
      idStr = $("#userId").`val`().toString
      if (idStr == "") {
        $(":input[name='name']").trigger("change")
      } else {
        $(":input[name='name']").`val`(idStr).trigger("change")
      }

      f()
    }
    $.ajax(ajaxSettings)

  }


  def pageChange(table: MyTable): js.Function = (number: Int, size: Int) => {
    bindEvt(table)
  }

  def initTable = {
    initLocalTable
    initBreedTable
    initWildTable
    initExIntroductionTable
  }

  def initLocalTable = {
    val table = LocalTable
    val checkColumn = ColumnOptions.field("checked").checkbox(true).formatter(checkFmt(table))
    val columnNames = js.Array("number", "name", "unitNumber", "comment")
    val columns = js.Array(checkColumn) ++ columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "统一编号"
        case "name" => "品种名称"
        case "unitNumber" => "单位编号"
        case "comment" => "备注"
        case _ => columnName
      }
      val fmt = table.tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).sortable(true).formatter(fmt)
    }
    val options = TableOptions.columns(columns).onPageChange(pageChange(table))
    $(s"#${table.id}").bootstrapTable(options)
  }

  def initBreedTable = {
    val table = BreedTable
    val checkColumn = ColumnOptions.field("checked").checkbox(true).formatter(checkFmt(table))
    val columnNames = js.Array("number", "name", "comment")
    val columns = js.Array(checkColumn) ++ columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "保存号"
        case "name" => "名称"
        case "comment" => "备注"
        case _ => columnName
      }
      val fmt = table.tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).sortable(true).formatter(fmt)
    }
    val options = TableOptions.columns(columns).onPageChange(pageChange(table))
    $(s"#${table.id}").bootstrapTable(options)
  }

  def initWildTable = {
    val table = WildTable
    val checkColumn = ColumnOptions.field("checked").checkbox(true).formatter(checkFmt(table))
    val columnNames = js.Array("number", "source", "comment")
    val columns = js.Array(checkColumn) ++ columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "原统一编号"
        case "source" => "采集/原产地"
        case "comment" => "备注"
        case _ => columnName
      }
      val fmt = table.tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).sortable(true).formatter(fmt)
    }
    val options = TableOptions.columns(columns).onPageChange(pageChange(table))
    $(s"#${table.id}").bootstrapTable(options)
  }

  def initExIntroductionTable = {
    val table = ExIntroductionTable
    val checkColumn = ColumnOptions.field("checked").checkbox(true).formatter(checkFmt(table))
    val columnNames = js.Array("number", "name", "sourceArea", "seedSource", "comment")
    val columns = js.Array(checkColumn) ++ columnNames.map { columnName =>
      val title = columnName match {
        case "number" => "统一编号"
        case "name" => "品种名称"
        case "sourceArea" => "原产地"
        case "seedSource" => "种子来源"
        case "comment" => "备注"
        case _ => columnName
      }
      val fmt = table.tbFmt(columnName)
      ColumnOptions.field(columnName).title(title).sortable(true).formatter(fmt)
    }
    val options = TableOptions.columns(columns).onPageChange(pageChange(table))
    $(s"#${table.id}").bootstrapTable(options)
  }

  def oncheck(table: MyTable): js.Function = (e: JQueryEventObject, row: js.Dictionary[js.Any], y: Element) => {
    val number = row.myGet("number")
    table.numbers += number
    updateLimit
  }

  def onUnCheck(table: MyTable): js.Function = (e: JQueryEventObject, row: js.Dictionary[js.Any], y: Element) => {
    val number = row.myGet("number")
    table.numbers -= number
    updateLimit
  }

  def onCheckAll(table: MyTable): js.Function = (e: JQueryEventObject, rowsAfter: js.Array[js.Dictionary[js.Any]], rowsBefore: js.Array[js.Dictionary[js.Any]]) => {
    val arrays = $(s"#${table.id}").bootstrapTable("getSelections").asInstanceOf[js.Array[js.Dictionary[String]]]
    val ids = arrays.map { x =>
      x("number")
    }
    table.numbers ++= ids.toSet
    updateLimit
  }

  def onUncheckAll(table: MyTable): js.Function = (e: JQueryEventObject, rowsBefore: js.Array[js.Dictionary[js.Any]], rowsAfter: js.Any) => {
    val ids = rowsBefore.map { x =>
      x("number").toString
    }
    table.numbers --= ids.toSet
    updateLimit
  }

  def numbersClear = {
    LocalTable.numbers.clear()
    BreedTable.numbers.clear()
    WildTable.numbers.clear()
    ExIntroductionTable.numbers.clear()
  }

  def refreshUserLimit(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.AdminController.getUserLimitById().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?id=${idStr}").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[String]]
      numbersClear
      LocalTable.numbers ++= rs("localSample").split(";").toSet
      BreedTable.numbers ++= rs("breedSample").split(";").toSet
      WildTable.numbers ++= rs("wildSample").split(";").toSet
      ExIntroductionTable.numbers ++= rs("exIntroductionSample").split(";").toSet
      f()
    }
    $.ajax(ajaxSettings)

  }

  def bindEvt(table: MyTable) = {
    val tableId = table.id
    jQuery(s"#${tableId}").on("check.bs.table", oncheck(table))
    $(s"#${tableId} tr[data-index] > td > input").on("mousedown", (y: Element, e: Event) => {
      val me = e.asInstanceOf[MouseEvent]
      isShifit = me.shiftKey
      shiftCheckbox(table)(y)
    })
    org.scalajs.jquery.jQuery(s"#${tableId}").on("uncheck.bs.table", onUnCheck(table))
    org.scalajs.jquery.jQuery(s"#${tableId}").on("check-all.bs.table", onCheckAll(table))
    org.scalajs.jquery.jQuery(s"#${tableId}").on("uncheck-all.bs.table", onUncheckAll(table))
  }

  def checkFmt(table: MyTable): js.Function = (v: js.Any, row: js.Dictionary[Any]) => {
    val number = row("number").toString
    val numbers = table.numbers
    if (numbers.contains(number)) {
      true
    } else false

  }

  def refreshTableData(f: () => js.Any = () => ()) = {
    val table = LocalTable
    val index = layer.alert(Tool.loadingElement, layerOptions)
    val url = g.jsRoutes.controllers.adminC.LocalSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      $(s"#${table.id}").bootstrapTable("load", rs)
      layer.close(index)
      f()
    }
    $.ajax(ajaxSettings)

  }

  def refreshBreedTableData(f: () => js.Any = () => ()) = {
    val table = BreedTable
    val url = g.jsRoutes.controllers.adminC.BreedSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      $(s"#${table.id}").bootstrapTable("load", rs)
      f()
    }
    $.ajax(ajaxSettings)

  }

  def refreshWildTableData(f: () => js.Any = () => ()) = {
    val table = WildTable
    val url = g.jsRoutes.controllers.adminC.WildSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      $(s"#${table.id}").bootstrapTable("load", rs)
      f()
    }
    $.ajax(ajaxSettings)

  }

  def refreshExIntroductionTableData(f: () => js.Any = () => ()) = {
    val table = ExIntroductionTable
    val url = g.jsRoutes.controllers.adminC.ExIntroductionController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get") success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      $(s"#${table.id}").bootstrapTable("load", rs)
      f()
    }
    $.ajax(ajaxSettings)

  }


  def shiftCheckbox(table: MyTable) = (y: Element) => {
    val lastChecked = table.lastChecked
    if (lastChecked == null) {
    } else {
      if (isShifit) {
        val start = $(y).attr("data-index").toString.toInt
        val end = $(lastChecked).attr("data-index").toString.toInt
        val status = $(lastChecked).is(":checked")
        val min = math.min(start, end)
        val max = math.max(start, end)
        (min until max).foreach { i =>
          if (status) {
            $(s"#${table.id}").bootstrapTable("check", i)
          } else {
            $(s"#${table.id}").bootstrapTable("uncheck", i)
          }
        }
      }
    }
    table.lastChecked = y

  }

  @JSExport("updateLimit")
  def updateLimit = {
    val url = g.jsRoutes.controllers.AdminController.refreshLimit().url.toString
    val data = js.Dictionary(
      "id" -> idStr,
      "localNumbers" -> LocalTable.numbers.toJSArray,
      "breedNumbers" -> BreedTable.numbers.toJSArray,
      "wildNumbers" -> WildTable.numbers.toJSArray,
      "exIntroductionNumbers" -> ExIntroductionTable.numbers.toJSArray,
    )
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
    }
    $.ajax(ajaxSettings)
  }

}
