package tool

/**
 * Created by Administrator on 2019/9/27
 */
object Pojo {

  case class StatData(phenotype: String, numbers: Seq[String])

  case class UserData(name: String, password: String)

  case class IdData(id: Int)

  case class IdOpData(id: Option[Int])

  case class UserLimitData(id: Int, localNumbers: Seq[String], breedNumbers: Seq[String], wildNumbers: Seq[String],
                           exIntroductionNumbers: Seq[String])


}
