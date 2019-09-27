package myJs.admin.localSample

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._
import shared.Shared

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("LocalSampleDetail")
object Detail {

  @JSExport("init")
  def init = {
    refreshPhenotypeView
    fillValue
    refreshImage

  }

  def fillValue = {
    val data = js.Dictionary(
      "number" -> $("#number").text().trim
    )
    val url = g.jsRoutes.controllers.LocalSampleController.getSamplePhenotypeInfo().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      rs.foreach { case (key, value) =>
        $(s"#${key}").text(value.toString)
      }
      val phenotypes = JSON.parse(rs.myGet("phenotype")).asInstanceOf[js.Dictionary[js.Any]]
      phenotypes.foreach { case (key, value) =>
        $(s"#${key}").text(value.toString)
      }

    }
    $.ajax(ajaxSettings)

  }

  def refreshPhenotypeView = {
    val phenotypeNames = Shared.localSamplePhenotypeNames
    val html = phenotypeNames.grouped(3).map { groups =>
      val groupInner = groups.map { x =>
        span(
          label(cls := "control-label col-sm-2", s"${x}:"),
          div(cls := "col-sm-2",
            p(cls := "form-control-static phenotype", id := x)
          )
        )
      }.mkString
      div(cls := "form-group",
        raw(groupInner)
      )
    }.mkString
    $("#phenotypeInfo").html(html)

  }

  def refreshImage = {
    val number=$("#number").text().trim
    val data = js.Dictionary(
      "number" -> number
    )
    val url = g.jsRoutes.controllers.LocalSampleController.getAllFiles().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val html = if (rs.nonEmpty) {
        rs.map { case (key, value) =>
          val imageUrl = g.jsRoutes.controllers.AppController.getImage().url.toString
          val desc = if (value.toString == "") "暂无" else value.toString
          div(cls := "col-sm-4",
            div(cls := "thumbnail",
              img(src := s"${imageUrl}?number=${number}&fileName=${key}&dir=${AppendixManage.dir}", height := 275),
              div(cls := "caption",
                h4(key.toString),
                h5("说明：", desc),
                p(
                  a(cls := "btn btn-primary", role := "button", onclick := s"LocalSampleAManage.show('${number}','${key}','${AppendixManage.dir}')", "查看"), raw("&nbsp;"),
                )
              )
            )
          )
        }.mkString
      } else {
        span(margin := 15, "该样品附件为空，可前往附件管理处上传！").render
      }
      $("#appendix").html(html)

    }
    $.ajax(ajaxSettings)

  }

}
