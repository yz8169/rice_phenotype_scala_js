package myJs.wildSample

import myJs.Tool._
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import plotly._
import plotly.element._
import plotly.layout._
import shared._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._


/**
  * Created by yz on 2019/3/6
  */
@JSExportTopLevel("WildSampleStat")
object WildSampleStat {
  val samples = Shared.wildSelectPhenotypes
  val phenotypeMap = Shared.wildSampleMap

  @JSExport("init")
  def init = {
    $(":input[name='phenotype']").select2(Select2Options.data(samples.toJSArray))
    refreshTypeahead

  }

  def refreshTypeahead = {
    val url = g.jsRoutes.controllers.WildSampleController.getAllNumber().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("get").success { (data, status, e) =>
      val numbers = data.asInstanceOf[js.Array[String]]
      val options=Select2Options.data(numbers).multiple(true)
      $(":input[name='numbers[]']").select2(options)
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("mySearch")
  def mySearch = {
    val index = layer.alert(myElement, layerOptions)
    $("#content").hide()
    val data = $(s"#form").serialize()
    val url = g.jsRoutes.controllers.WildSampleController.getStatData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).data(data).
      `type`("post").success { (data, status, e) =>
      $("#content").show()
      val key = $(":input[name='phenotype']").`val`().toString
      val filterArray = data.asInstanceOf[js.Array[js.Dictionary[String]]]
      if (phenotypeMap(key) == "数量") {
        val datas = filterArray.map { row =>
          row(key)
        }.toSeq.map(x => x.toDouble)
        val data = Seq(
          Box(
            y = datas,
            name = key
          )
        )
        val layout = Layout(
          title = s"箱式图"
        )
        val config = PlotlyConfigOptions.displayModeBar(false)
        MyPlotly.newPlot("chart", data, layout, config)
      } else {
        val map = filterArray.map { row =>
          row(key).toString
        }.groupBy(x => x).mapValues(values => values.size)
        val data = Seq(
          Bar(
            map.keySet.toSeq,
            map.values.toSeq
          )
        )
        val layout = Layout(
          title = s"柱状图",
          xaxis = Axis(
            title = s"${key}",
            `type` = AxisType.Category
          ),
          yaxis = Axis(
            title = s"数量"
          )
        )
        val config = PlotlyConfigOptions.displayModeBar(false)
        MyPlotly.newPlot("chart", data, layout, config)
      }
      layer.close(index)

    }
    $.ajax(ajaxSettings)

  }

}
