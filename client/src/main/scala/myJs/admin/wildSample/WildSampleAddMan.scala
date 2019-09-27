package myJs.admin.wildSample

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("WildSampleAddMan")
object WildSampleAddMan {

  @JSExport("init")
  def init = {
    refreshPhenotypeView
    bootStrapValidator

  }

  @JSExport("add")
  def add = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = getData
      val url = g.jsRoutes.controllers.adminC.WildSampleController.addSamplePhenotype().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
        contentType("application/json").success { (data, status, e) =>
        g.swal("成功！", "新增成功！", "success")
        g.$("#form").bootstrapValidator("revalidateField", "number")

      }
      $.ajax(ajaxSettings)

    }

  }

  def getData = {
    val phenotypes = $(".phenotype").mapElems { y =>
      val key = $(y).attr("name").toString
      val value = $(y).`val`()
      Map(key -> value)
    }.reduceLeft(_ ++ _).toJSDictionary
    js.Dictionary(
      "number" -> $("input[name='number']").`val`(),
      "source" -> $("input[name='source']").`val`(),
      "comment" -> $("textarea[name='comment']").`val`(),
      "phenotype" -> JSON.stringify(phenotypes)
    )
  }

  def refreshPhenotypeView = {
    val phenotypeNames = Shared.wildSamplePhenotypeNames
    val html = phenotypeNames.grouped(3).map { groups =>
      val groupInner = groups.map { x =>
        span(
          label(cls := "control-label col-sm-2", s"${x}:"),
          div(cls := "col-sm-2",
            input(cls := "form-control phenotype", name := x)
          )
        )
      }.mkString
      div(cls := "form-group",
        raw(groupInner)
      )
    }.mkString
    $("#phenotype").html(html)

  }

  def bootStrapValidator = {
    val url = g.jsRoutes.controllers.adminC.WildSampleController.numberCheck().url.toString
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
        "number" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "原统一编号不能为空!"
            ),
            "remote" -> js.Dictionary(
              "message" -> "原统一编号已存在!",
              "url" -> url,
              "delay" -> 1000,
            ),
          )
        ),
        "source" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "采集/原产地不能为空!"
            )
          )
        ),

      )
    )
    g.$("#form").bootstrapValidator(dict)

  }


}
