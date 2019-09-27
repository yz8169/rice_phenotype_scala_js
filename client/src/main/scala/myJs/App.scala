package myJs

import com.karasiq.bootstrap.Bootstrap.default._
import org.querki.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
  * Created by yz on 2019/4/25
  */
@JSExportTopLevel("App")
object App {

  @JSExport("init")
  def init={
    val shareTitle="水稻种质资源数据库"
    val beforeTitle=$("title").text()
    $("title").text(s"${beforeTitle}-${shareTitle}")

  }

}
