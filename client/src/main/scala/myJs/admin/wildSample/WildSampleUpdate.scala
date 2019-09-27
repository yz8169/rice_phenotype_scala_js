package myJs.admin.wildSample

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import org.querki.jquery._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("WildSampleUpdate")
object WildSampleUpdate {

  @JSExport("init")
  def init = {
    WildSampleAddMan.refreshPhenotypeView
    fillValue
    bootStrapValidator

  }

  def fillValue = {
    val data = js.Dictionary(
      "number" -> $("input[name='number']").`val`()
    )
    val url = g.jsRoutes.controllers.WildSampleController.getSamplePhenotypeInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      rs.foreach { case (key, value) =>
        $(s"input[name='${key}']").`val`(value.toString)
        $(s"textarea[name='${key}']").`val`(value.toString)
      }
      val phenotypes = JSON.parse(rs.myGet("phenotype")).asInstanceOf[js.Dictionary[js.Any]]
      phenotypes.foreach { case (key, value) =>
        $(s"input[name='${key}']").`val`(value.toString)
      }

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("update")
  def update = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = WildSampleAddMan.getData
      val url = g.jsRoutes.controllers.adminC.WildSampleController.updateSamplePhenotype().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
        contentType("application/json").success { (data, status, e) =>
        g.swal("成功！", "更新成功！", "success")
      }
      $.ajax(ajaxSettings)

    }

  }

  def bootStrapValidator = {
    val dict = js.Dictionary(
      "feedbackIcons" -> js.Dictionary(
        "valid" -> "glyphicon glyphicon-ok",
        "invalid" -> "glyphicon glyphicon-remove",
        "validating" -> "glyphicon glyphicon-refresh",
      ),
      "fields" -> js.Dictionary(
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
