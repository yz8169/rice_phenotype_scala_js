package myJs.wildSample

import myJs.Utils._
import myJs.admin.wildSample._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("WildSampleFDetail")
object WildSampleFDetail {

  @JSExport("init")
  def init = {
    WildSampleDetail.refreshPhenotypeView
    WildSampleDetail.fillValue
    refreshImage

  }

  def refreshImage = {
    val number = $("#number").text().trim
    val data = js.Dictionary(
      "number" -> number
    )
    val url = g.jsRoutes.controllers.WildSampleController.getAllFiles().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val html = if (rs.nonEmpty) {
        rs.map { case (key, value) =>
          val imageUrl = g.jsRoutes.controllers.AppController.getImage().url.toString
          val desc = if (value.toString == "") "暂无" else value.toString
          div(cls := "col-sm-4",
            div(cls := "thumbnail",
              img(src := s"${imageUrl}?number=${number}&fileName=${key}&dir=${WildSampleAManage.dir}", height := 275),
              div(cls := "caption",
                h4(key.toString),
                h5("说明：", desc),
                p(
                  a(cls := "btn btn-primary", role := "button", onclick := s"LocalSampleAManage.show('${number}','${key}','${WildSampleAManage.dir}')", "查看"), raw("&nbsp;"),
                )
              )
            )
          )
        }.mkString
      } else {
        span(margin := 15, "该样品附件为空！").render
      }
      $("#appendix").html(html)

    }
    $.ajax(ajaxSettings)

  }

}
