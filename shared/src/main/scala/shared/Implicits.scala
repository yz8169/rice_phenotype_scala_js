package shared

/**
  * Created by yz on 2019/5/9
  */
object Implicits {

  implicit class MyString(v: String) {

    def isDouble: Boolean = {
      try {
        v.toDouble
      } catch {
        case _: Exception =>
          return false
      }
      true
    }

    def toDoubleOp = {
      if (v.isEmpty) {
        None
      } else {
        Some(v.toDouble)
      }
    }

    def toOp={
      if(v.isBlank){
        None
      }else Some(v)
    }

    def isBlank= v==null || v.trim.isEmpty


  }


}
