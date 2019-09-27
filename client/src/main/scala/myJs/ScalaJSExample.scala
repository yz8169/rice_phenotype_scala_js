package myJs

import org.scalajs.dom
import org.querki.jquery._

import scala.scalajs.js.annotation._

@JSExportTopLevel("ScalaJsExample")
object ScalaJSExample {

  def main(args: Array[String]): Unit = {

  }

  def setupUI = {
    appendPar("Hello World")
    $("#click-me-button").click(() => addClickedMessage)
  }

  def appendPar(text: String) = {
    $("body").append(s"<p>${text}</p>")
  }

  def addClickedMessage = {
    appendPar("You clicked the button!")
  }


}
