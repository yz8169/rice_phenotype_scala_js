package myJs.localSample

import myJs.Utils._
import myJs.admin.localSample.Manage
import org.querki.jquery._
import scalatags.Text.all._
import shared._

import scala.scalajs.js
import scala.scalajs.js.annotation._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.scalajs.dom.Element
import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Tool._
import scalajs.js.JSConverters._
import shared.Implicits._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("LocalSampleSearch")
object LocalSampleSearch {
  var i = 0
  var j = 0
  var array: js.Array[js.Dictionary[String]] = _

  @JSExport("init")
  def init = {
    LocalSampleView.refreshTableView
    refreshTableData()
    bootStrapValidator
    refreshSelect

  }

  def refreshSelect = {
    $("#content").empty()
    i = 0
    j = 0
    addSelect

  }

  def refreshTableData(f: () => js.Any = () => ()) = {
    val index = layer.alert(myElement, layerOptions)
    val url = g.jsRoutes.controllers.LocalSampleController.getAllPhenotype().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get").async(false).success { (data, status, e) =>
      array = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      $("#table").bootstrapTable("load", data)
      f()
      layer.close(index)
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("change")
  def change(y: Element, id: String) = {
    val value = $(y).find(">option:selected").`val`().toString
    val iValue = id.replaceAll("content", "").toInt
    var html = getHtml(value, iValue)
    $("#" + id).empty().append(html)
    g.$("#form").bootstrapValidator("addField", $("#start" + iValue))
    g.$("#form").bootstrapValidator("addField", $("#end" + iValue))
    $(".js-example-basic-multiple").select2(Select2Options.placeholder("点击选择质量性状"))

  }

  @JSExport("addSelect")
  def addSelect = {
    i += 1
    j += 1
    val samples = Shared.localSampleMap.keys.toList
    val phenotypeHtml = samples.map { v =>
      option(value := v, v)
    }.mkString("&nbsp;")
    val htmlAfter = getHtml(samples(0), i)
    val html = div(cls := "form-group", id := s"select${i}",
      label(cls := "control-label col-sm-2", s"条件${i}："),
      div(cls := "col-sm-2",
        select(cls := "form-control", name := "phenotypeNames[]", id := s"phenotype${i}",
          onclick := s"LocalSampleSearch.change(this,'content${i}')", raw(phenotypeHtml)),
      ),
      div(id := s"content${i}", raw(htmlAfter)),
      div(cls := "col-sm-2",
        button(`type` := "button", cls := "btn btn-default deleteButton", width := "100%", onclick := s"LocalSampleSearch.deleteSelect('${i}')", "删除此条件"),
      ),
    ).render
    $("#content").append(html)
    setDisable
    g.$("#form").bootstrapValidator("addField", $("#start" + i))
    g.$("#form").bootstrapValidator("addField", $("#end" + i))
    $(".js-example-basic-multiple").select2(Select2Options.placeholder("点击选择质量性状"))

  }

  def setDisable = {
    if (j < 2) {
      $(".deleteButton").attr("disabled", true)
    } else {
      $(".deleteButton").attr("disabled", false)
    }

  }

  @JSExport("deleteSelect")
  def deleteSelect(value: String) = {
    $("#select" + value).remove()
    j -= 1
    setDisable
    g.$("#form").bootstrapValidator("removeField", $("#start" + value))
    g.$("#form").bootstrapValidator("removeField", $("#end" + value))

  }

  @JSExport("mySearch")
  def mySearch = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    val inputDict = $(":input[name='phenotypeNames[]']").mapElems { (y: Element) =>
      val name = $(y).`val`().toString
      val iValue = $(y).attr("id").toString.replaceAll("phenotype", "")
      val dict = if (Shared.localSampleMap(name) == "数量") {
        val min = $(s"#start${iValue}").`val`()
        val max = $(s"#end${iValue}").`val`()
        js.Dictionary("min" -> min, "max" -> max)
      } else {
        $(s"#${iValue}").`val`()
      }
      (name, dict)
    }
    if (valid && checkAll(inputDict)) {
      val index = layer.alert(myElement, layerOptions)
      val newArray = array.filter { row =>
        inputDict.forall { case (key, dict) =>
          val dbValue = row(key)
          if (!dbValue.isEmpty) {
            if (Shared.localSampleMap(key) == "数量") {
              val valueDict = dict.asInstanceOf[js.Dictionary[String]]
              val minOp = valueDict("min").toDoubleOp
              val maxOp = valueDict("max").toDoubleOp
              val dbDouble = dbValue.toDouble
              minOp.map { min =>
                dbDouble >= min
              }.getOrElse(true) && maxOp.map { max =>
                dbDouble <= max
              }.getOrElse(true)
            } else {
              val values = dict.asInstanceOf[js.Array[String]]
              if (values != null) {
                values.contains(dbValue)
              } else true
            }

          } else false

        }
      }
      $("#table").bootstrapTable("load", newArray)
      layer.close(index)

    }

  }

  def checkAll(inputDict: Seq[(String, js.Any)]) = {
    val op = inputDict.find { case (key, dict) =>
      if (Shared.localSampleMap(key) == "数量") {
        val valueDict = dict.asInstanceOf[js.Dictionary[String]]
        val minOp = valueDict("min").toDoubleOp
        val maxOp = valueDict("max").toDoubleOp
        if (minOp.isDefined && maxOp.isDefined) {
          minOp.get > maxOp.get
        } else false
      } else false

    }
    if (op.isDefined) {
      Swal.swal(SwalOptions.title("错误").text(s"条件 ${op.get._1} 最大值必须大于等于最小值!").`type`("error"))
      false
    } else true


  }

  def getHtml(vStr: String, i: Int) = {
    val kind = Shared.localSampleMap(vStr)
    if (kind == "数量") {
      div(
        div(cls := "col-sm-2",
          input(cls := "form-control", name := "start[]", placeholder := "最小值", id := s"start${i}")
        ),
        div(cls := "col-sm-2",
          input(cls := "form-control", name := "end[]", placeholder := "最大值", id := s"end${i}")
        ),
      ).render
    } else {
      val values = getValues(vStr)
      val optionHtml = values.map { v =>
        option(value := v, v)
      }.mkString("&nbsp;")
      div(
        div(cls := "col-sm-4",
          select(cls := "js-example-basic-multiple form-control", multiple := "multiple", name := "selects[]", id := s"${i}",
            raw(optionHtml))
        ),
      ).render
    }

  }

  def getValues(vStr: String) = {
    array.map { dict =>
      dict(vStr)
    }.distinct

  }


  def bootStrapValidator = {
    val url = g.jsRoutes.controllers.adminC.LocalSampleController.numberCheck().url.toString
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
        "start[]" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "最小值必须为数字！"
            ),

          )
        ),
        "end[]" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "numeric" -> js.Dictionary(
              "message" -> "最大值必须为数字！"
            ),
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }

}
