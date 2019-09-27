package myJs.admin.localSample

import org.querki.jquery._
import com.karasiq.bootstrap.Bootstrap.default._

import scala.scalajs.js.annotation._
import shared.Shared
import scalatags.Text.all._
import myJs.Utils._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scalajs.js.JSConverters._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("LocalSampleAddMan")
object AddMan {

  @JSExport("init")
  def init = {
    val phenotypeNames = Shared.localSamplePhenotypeNames
    AddMan.refreshPhenotypeView
    bootStrapValidator

  }

  @JSExport("add")
  def add = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = getData
      val url = g.jsRoutes.controllers.adminC.LocalSampleController.addSamplePhenotype().url.toString
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
      "name" -> $("input[name='name']").`val`(),
      "unitNumber" -> $("input[name='unitNumber']").`val`(),
      "comment" -> $("textarea[name='comment']").`val`(),
      "phenotype" -> JSON.stringify(phenotypes)
    )
  }

  def refreshPhenotypeView = {
    val phenotypeNames = Shared.localSamplePhenotypeNames
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
    val url = g.jsRoutes.controllers.adminC.LocalSampleController.numberCheck().url.toString
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
              "message" -> "统一编号不能为空!"
            ),
            "remote" -> js.Dictionary(
              "message" -> "统一编号已存在!",
              "url" -> url,
              "delay" -> 1000,
            ),
          )
        ),
        "name" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "品种名称不能为空!"
            )
          )
        ),
        "unitNumber" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "单位编号不能为空!"
            )
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }


}
