package myJs

import org.scalajs.dom.Element

import scala.collection.mutable
import scala.scalajs.js

/**
 * Created by Administrator on 2019/10/8
 */
object Pojo {

  trait MyTable {
    val numbers: mutable.Set[String] = mutable.Set[String]()
    val id: String
    var lastChecked: Element = null

    def tbFmt(columnName: String): js.Function

  }

  case object LocalTable extends MyTable {
    override val id: String = "table"

    override def tbFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[Any]) => columnName match {
      case "number" => {
        Tool.numberA(v.toString)
      }
      case _ => v
    }
  }

  case object BreedTable extends MyTable {
    override val id: String = "breedTable"

    override def tbFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[Any]) => columnName match {
      case "number" => {
        Tool.breedNumberA(v.toString)
      }
      case _ => v
    }
  }

  case object WildTable extends MyTable {
    override val id: String = "wildTable"

    override def tbFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[Any]) => columnName match {
      case "number" => {
        Tool.wildNumberA(v.toString)
      }
      case _ => v
    }
  }

  case object ExIntroductionTable extends MyTable {
    override val id: String = "exIntroductionTable"

    override def tbFmt(columnName: String): js.Function = (v: js.Any, row: js.Dictionary[Any]) => columnName match {
      case "number" => {
        Tool.exNumberA(v.toString)
      }
      case _ => v
    }
  }

}
