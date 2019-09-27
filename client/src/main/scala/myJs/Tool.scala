package myJs

import myJs.myPkg._
import org.querki.jquery.$
import org.scalajs.dom.raw.ProgressEvent
import scalatags.Text.all._

import scala.math.BigDecimal.RoundingMode
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/3/6
  */
object Tool {

  val zhInfo = "信息"
  val layerOptions = LayerOptions.title(zhInfo).closeBtn(0).skin("layui-layer-molv").btn(js.Array())

  val zhRunning = "正在运行"
  val myElement = div(id := "content")(
    span(id := "info")(zhRunning),
    " ",
    img(src := "/assets/images/running2.gif", width := 30, height := 20, cls := "runningImage")
  ).render

  def extractor(query: String) = {
    val result = js.RegExp("([^,]+)$").exec(query)
    if (result != null && result(1).isDefined) {
      result(1).toString.trim
    } else ""

  }

  val highlighterF = (y: Typeahead, item: String) => {
    val input = y.query
    val query = extractor(input).replaceAll("[\\-\\[\\]{}()*+?.,\\\\^$|#\\s]", "\\$&")
    item.replaceAll(s"(?i)${query}", s"<strong>${query}</strong>")
  }


}
