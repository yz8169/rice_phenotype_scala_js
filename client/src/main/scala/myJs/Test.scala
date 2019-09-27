package myJs

import org.querki.jquery._

import scala.scalajs.js.annotation._

@JSExportTopLevel("Test")
object Test {

  trait Animal{
    val age=17
  }

  class Person extends Animal{
    override val age: Int = 20
  }

  trait Man extends Animal{
    override val age: Int = 25
  }


  @JSExport("init")
  def init = {
    val man=new Person
    println(man.age)
  }

  def appendPar(text: String) = {
    $("body").append(s"<p>${text}</p>")
  }

  def addClickedMessage = {
    appendPar("You clicked the button!")
  }


}
