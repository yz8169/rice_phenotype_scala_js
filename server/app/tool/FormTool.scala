package tool

import models.Tables.ExIntroductionRow
import play.api.data._
import play.api.data.Forms._
import tool.Pojo._

/**
  * Created by yz on 2019/1/18
  */
class FormTool {

  case class AccountData(account: String, password: String)

  val accountForm = Form(
    mapping(
      "account" -> text,
      "password" -> text
    )(AccountData.apply)(AccountData.unapply)
  )

  case class ChangePasswordData(password: String, newPassword: String)

  val changePasswordForm = Form(
    mapping(
      "password" -> text,
      "newPassword" -> text
    )(ChangePasswordData.apply)(ChangePasswordData.unapply)
  )

  case class FileNameData(fileName: String)

  val fileNameForm = Form(
    mapping(
      "fileName" -> text
    )(FileNameData.apply)(FileNameData.unapply)
  )

  case class NumberData(number: String)

  val numberForm = Form(
    mapping(
      "number" -> text
    )(NumberData.apply)(NumberData.unapply)
  )

  val statForm = Form(
    mapping(
      "phenotype" -> text,
      "numbers" -> seq(text),
    )(StatData.apply)(StatData.unapply)
  )

  case class NumbersData(numbers: Seq[String])

  val numbersForm = Form(
    mapping(
      "numbers" -> seq(text)
    )(NumbersData.apply)(NumbersData.unapply)
  )

 val userLimitForm = Form(
    mapping(
      "id" -> number,
      "numbers" -> seq(text)
    )(UserLimitData.apply)(UserLimitData.unapply)
  )

  case class LocalSampleData(number: String, name: String, unitNumber: String, phenotype: String, comment: String)

  val localSampleForm = Form(
    mapping(
      "number" -> text,
      "name" -> text,
      "unitNumber" -> text,
      "phenotype" -> text,
      "comment" -> text
    )(LocalSampleData.apply)(LocalSampleData.unapply)
  )

  case class BreedSampleData(number: String, name: String, phenotype: String, comment: String)

  val breedSampleForm = Form(
    mapping(
      "number" -> text,
      "name" -> text,
      "phenotype" -> text,
      "comment" -> text
    )(BreedSampleData.apply)(BreedSampleData.unapply)
  )

  case class WildSampleData(number: String, source: String, phenotype: String, comment: String)

  val wildSampleForm = Form(
    mapping(
      "number" -> text,
      "source" -> text,
      "phenotype" -> text,
      "comment" -> text
    )(WildSampleData.apply)(WildSampleData.unapply)
  )

  case class ExIntroductionData(number: String, warehouseNumber: String, unitNumber: String, name: String, oldName: String,
                                family: String, genus: String, scientificName: String, sourceArea: String, seedSource: String,
                                storeUnit: String, originalNumber: String, storeUnit2: String, province: String, sampleKind: String,
                                phenotype: String,comment: String)

  val exIntroductionForm = Form(
    mapping(
      "number" -> text,
      "warehouseNumber" -> text,
      "unitNumber" -> text,
      "name" -> text,
      "oldName" -> text,
      "family" -> text,
      "genus" -> text,
      "scientificName" -> text,
      "sourceArea" -> text,
      "seedSource" -> text,
      "storeUnit" -> text,
      "originalNumber" -> text,
      "storeUnit2" -> text,
      "province" -> text,
      "sampleKind" -> text,
      "phenotype" -> text,
      "comment" -> text
    )(ExIntroductionData.apply)(ExIntroductionData.unapply)
  )

  case class AppendixData(number: String, describes: Seq[String])

  val appendixForm = Form(
    mapping(
      "number" -> text,
      "describes" -> seq(text)
    )(AppendixData.apply)(AppendixData.unapply)
  )

  case class ImageData(dir: String, number: String, fileName: String)

  val imageForm = Form(
    mapping(
      "dir" -> text,
      "number" -> text,
      "fileName" -> text
    )(ImageData.apply)(ImageData.unapply)
  )

  case class DescData(number: String, fileName: String, describe: String)

  val descForm = Form(
    mapping(
      "number" -> text,
      "fileName" -> text,
      "describe" -> text,
    )(DescData.apply)(DescData.unapply)
  )

  case class userNameData(name: String)

  val userNameForm = Form(
    mapping(
      "name" -> text
    )(userNameData.apply)(userNameData.unapply)
  )

  val userForm = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(UserData.apply)(UserData.unapply)
  )

  val idForm = Form(
    mapping(
      "id" -> number
    )(IdData.apply)(IdData.unapply)
  )


}
