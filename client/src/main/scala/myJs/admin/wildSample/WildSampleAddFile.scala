package myJs.admin.wildSample

import myJs.Utils._
import org.scalajs.dom.raw.{HTMLFormElement, XMLHttpRequest}
import org.scalajs.dom.{FormData, document}
import org.scalajs.jquery.jQuery
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.annotation._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("WildSampleAddFile")
object WildSampleAddFile {

  @JSExport("init")
  def init = {
    bootStrapValidator

  }

  @JSExport("add")
  def add = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val formData = new FormData(document.getElementById("form").asInstanceOf[HTMLFormElement])
      val element = div(id := "content",
        span(id := "info", "正在上传数据文件",
          span(id := "progress", "。。。")), " ",
        img(src := "/assets/images/running2.gif", cls := "runningImage", width := 30, height := 20)
      ).render
      val dict = js.Dictionary("skin" -> "layui-layer-molv",
        "closeBtn" -> 0,
        "title" -> "信息",
        "btn" -> js.Array(),
      )
      val index = g.layer.alert(element, dict)
      val url = g.jsRoutes.controllers.adminC.WildSampleController.addSamplePhenotypeByFile().url.toString
      val xhr = new XMLHttpRequest
      xhr.open("post", url)
      xhr.upload.onprogress = progressHandlingFunction
      xhr.onreadystatechange = (e) => {
        if (xhr.readyState == XMLHttpRequest.DONE) {
          val data = xhr.response
          val rs = data.asInstanceOf[js.Dictionary[js.Any]]
          g.layer.close(index)
          if (rs.get("error").isEmpty) {
            g.layer.alert("数据库更新完成", js.Dictionary(
              "skin" -> "layui-layer-molv",
              "closeBtn" -> 0,
            ))
          } else {
            g.swal("错误", rs("error"), "error")
          }
        }
      }
      xhr.send(formData)
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
        "file" -> js.Dictionary(
          "validators" -> js.Dictionary(
            "notEmpty" -> js.Dictionary(
              "message" -> "请选择一个数据文件!"
            ),
            "file" -> js.Dictionary(
              "message" -> "请选择一个Excel(*.xlsx)格式的数据文件!",
              "extension" -> "xlsx",
            ),
          )
        ),
      )
    )
    g.$("#form").bootstrapValidator(dict)

  }

}
