package myJs.admin.localSample

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import org.querki.jquery._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import scalatags.Text.all._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("LocalSampleAManage")
object AppendixManage {

  val dir="local_sample"

  @JSExport("init")
  def init = {
    refreshImage

  }

  @JSExport("updateDescBefore")
  def updateDescBefore(fileName: String, describe: String) = {
    $("input[name='fileName']").`val`(fileName)
    $("input[name='describe']").`val`(describe)
    jQuery("#updateDescModal").modal("show")

  }

  @JSExport("refreshImage")
  def refreshImage = {
    val number = $("input[name='number']").`val`()
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
              img(src := s"${imageUrl}?number=${number}&fileName=${key}&dir=${dir}", height := 275),
              div(cls := "caption",
                h4(key.toString),
                h5("说明：", desc,
                  raw("&nbsp;"),
                  a(title := "更改说明", onclick := s"LocalSampleAManage.updateDescBefore('${key}','${value}')", cursor.pointer,
                    span(
                      em(cls := "fa fa-edit")
                    )
                  )
                ),
                p(
                  a(cls := "btn btn-primary", role := "button", onclick := s"LocalSampleAManage.show('${number}','${key}','${dir}')", "查看"), raw("&nbsp;"),
                  a(cls := "btn btn-default", role := "button", onclick := s"LocalSampleAManage.delete('${number}','${key}')", "删除"), raw("&nbsp;"),
                )
              )
            )
          )
        }.mkString
      } else {
        span(margin := 20, "该样品附件为空，请先上传！").render
      }
      $("#appendix").html(html)

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("delete")
  def delete(number: String, fileName: String) = {
    val dict = js.Dictionary(
      "title" -> "",
      "text" -> "确定要此附件吗？",
      "type" -> "warning",
      "showCancelButton" -> true,
      "showConfirmButton" -> true,
      "confirmButtonClass" -> "btn-danger",
      "confirmButtonText" -> "确定",
      "closeOnConfirm" -> false,
      "cancelButtonText" -> "取消",
    )
    g.swal(dict, () => {
      val url = g.jsRoutes.controllers.adminC.LocalSampleController.deleteAppendix().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?number=${number}&fileName=${fileName}&dir=${dir}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshImage
        g.swal("成功！", "删除附件成功！", "success")
      }.error { (data, status, e) =>
        g.swal("错误", "删除失败！", "error")
      }
      $.ajax(ajaxSettings)
    })

  }

  @JSExport("show")
  def show(number: String, fileName: String,dir:String) = {
    val url = g.jsRoutes.controllers.AppController.getImage().url.toString
    val imageUrl = s"${url}?number=${number}&fileName=${fileName}&dir=${dir}"
    $("#image").attr("src", imageUrl)
    $("#title").html(fileName)
    jQuery("#myModal").modal(js.Dictionary(
      "keyboard" -> true
    ))

  }


  @JSExport("addBefore")
  def addBefore = {
    g.$("#input-1").fileinput("clear")
    jQuery("#addModal").modal("show")

  }

  @JSExport("updateDesc")
  def updateDesc = {
    val url = g.jsRoutes.controllers.adminC.LocalSampleController.updateDesc().url.toString
    val data = js.Dictionary(
      "number" -> $("input[name='number']").`val`(),
      "fileName" -> $("input[name='fileName']").`val`(),
      "describe" -> $("input[name='describe']").`val`(),
    )
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      refreshImage
      jQuery("#updateDescModal").modal("hide")
      g.swal("成功！", "附件说明更改成功！", "success")
    }
    $.ajax(ajaxSettings)

  }


}
