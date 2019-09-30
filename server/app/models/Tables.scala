package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Account.schema, BreedSample.schema, ExIntroduction.schema, LocalSample.schema, Mode.schema, User.schema, UserLimit.schema, WildSample.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Account
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param account Database column account SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true) */
  case class AccountRow(id: Int, account: String, password: String)
  /** GetResult implicit for fetching AccountRow objects using plain SQL queries */
  implicit def GetResultAccountRow(implicit e0: GR[Int], e1: GR[String]): GR[AccountRow] = GR{
    prs => import prs._
    AccountRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table account. Objects of this class serve as prototypes for rows in queries. */
  class Account(_tableTag: Tag) extends profile.api.Table[AccountRow](_tableTag, Some("rice_phenotype"), "account") {
    def * = (id, account, password) <> (AccountRow.tupled, AccountRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(account), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> AccountRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column account SqlType(VARCHAR), Length(255,true) */
    val account: Rep[String] = column[String]("account", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Account */
  lazy val Account = new TableQuery(tag => new Account(tag))

  /** Entity class storing rows of table BreedSample
   *  @param number Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param phenotype Database column phenotype SqlType(TEXT)
   *  @param comment Database column comment SqlType(VARCHAR), Length(255,true)
   *  @param photo Database column photo SqlType(TEXT) */
  case class BreedSampleRow(number: String, name: String, phenotype: String, comment: String, photo: String)
  /** GetResult implicit for fetching BreedSampleRow objects using plain SQL queries */
  implicit def GetResultBreedSampleRow(implicit e0: GR[String]): GR[BreedSampleRow] = GR{
    prs => import prs._
    BreedSampleRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table breed_sample. Objects of this class serve as prototypes for rows in queries. */
  class BreedSample(_tableTag: Tag) extends profile.api.Table[BreedSampleRow](_tableTag, Some("rice_phenotype"), "breed_sample") {
    def * = (number, name, phenotype, comment, photo) <> (BreedSampleRow.tupled, BreedSampleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(number), Rep.Some(name), Rep.Some(phenotype), Rep.Some(comment), Rep.Some(photo)).shaped.<>({r=>import r._; _1.map(_=> BreedSampleRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val number: Rep[String] = column[String]("number", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column phenotype SqlType(TEXT) */
    val phenotype: Rep[String] = column[String]("phenotype")
    /** Database column comment SqlType(VARCHAR), Length(255,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(255,varying=true))
    /** Database column photo SqlType(TEXT) */
    val photo: Rep[String] = column[String]("photo")
  }
  /** Collection-like TableQuery object for table BreedSample */
  lazy val BreedSample = new TableQuery(tag => new BreedSample(tag))

  /** Entity class storing rows of table ExIntroduction
   *  @param number Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param warehouseNumber Database column warehouse_number SqlType(VARCHAR), Length(255,true)
   *  @param unitNumber Database column unit_number SqlType(VARCHAR), Length(255,true)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param oldName Database column old_name SqlType(VARCHAR), Length(255,true)
   *  @param family Database column family SqlType(VARCHAR), Length(255,true)
   *  @param genus Database column genus SqlType(VARCHAR), Length(255,true)
   *  @param scientificName Database column scientific_name SqlType(VARCHAR), Length(255,true)
   *  @param sourceArea Database column source_area SqlType(VARCHAR), Length(255,true)
   *  @param seedSource Database column seed_source SqlType(VARCHAR), Length(255,true)
   *  @param storeUnit Database column store_unit SqlType(VARCHAR), Length(255,true)
   *  @param originalNumber Database column original_number SqlType(VARCHAR), Length(255,true)
   *  @param storeUnit2 Database column store_unit_2 SqlType(VARCHAR), Length(255,true)
   *  @param province Database column province SqlType(VARCHAR), Length(255,true)
   *  @param sampleKind Database column sample_kind SqlType(VARCHAR), Length(255,true)
   *  @param phenotype Database column phenotype SqlType(TEXT)
   *  @param comment Database column comment SqlType(VARCHAR), Length(255,true)
   *  @param photo Database column photo SqlType(TEXT) */
  case class ExIntroductionRow(number: String, warehouseNumber: String, unitNumber: String, name: String, oldName: String, family: String, genus: String, scientificName: String, sourceArea: String, seedSource: String, storeUnit: String, originalNumber: String, storeUnit2: String, province: String, sampleKind: String, phenotype: String, comment: String, photo: String)
  /** GetResult implicit for fetching ExIntroductionRow objects using plain SQL queries */
  implicit def GetResultExIntroductionRow(implicit e0: GR[String]): GR[ExIntroductionRow] = GR{
    prs => import prs._
    ExIntroductionRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table ex_introduction. Objects of this class serve as prototypes for rows in queries. */
  class ExIntroduction(_tableTag: Tag) extends profile.api.Table[ExIntroductionRow](_tableTag, Some("rice_phenotype"), "ex_introduction") {
    def * = (number, warehouseNumber, unitNumber, name, oldName, family, genus, scientificName, sourceArea, seedSource, storeUnit, originalNumber, storeUnit2, province, sampleKind, phenotype, comment, photo) <> (ExIntroductionRow.tupled, ExIntroductionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(number), Rep.Some(warehouseNumber), Rep.Some(unitNumber), Rep.Some(name), Rep.Some(oldName), Rep.Some(family), Rep.Some(genus), Rep.Some(scientificName), Rep.Some(sourceArea), Rep.Some(seedSource), Rep.Some(storeUnit), Rep.Some(originalNumber), Rep.Some(storeUnit2), Rep.Some(province), Rep.Some(sampleKind), Rep.Some(phenotype), Rep.Some(comment), Rep.Some(photo)).shaped.<>({r=>import r._; _1.map(_=> ExIntroductionRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val number: Rep[String] = column[String]("number", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column warehouse_number SqlType(VARCHAR), Length(255,true) */
    val warehouseNumber: Rep[String] = column[String]("warehouse_number", O.Length(255,varying=true))
    /** Database column unit_number SqlType(VARCHAR), Length(255,true) */
    val unitNumber: Rep[String] = column[String]("unit_number", O.Length(255,varying=true))
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column old_name SqlType(VARCHAR), Length(255,true) */
    val oldName: Rep[String] = column[String]("old_name", O.Length(255,varying=true))
    /** Database column family SqlType(VARCHAR), Length(255,true) */
    val family: Rep[String] = column[String]("family", O.Length(255,varying=true))
    /** Database column genus SqlType(VARCHAR), Length(255,true) */
    val genus: Rep[String] = column[String]("genus", O.Length(255,varying=true))
    /** Database column scientific_name SqlType(VARCHAR), Length(255,true) */
    val scientificName: Rep[String] = column[String]("scientific_name", O.Length(255,varying=true))
    /** Database column source_area SqlType(VARCHAR), Length(255,true) */
    val sourceArea: Rep[String] = column[String]("source_area", O.Length(255,varying=true))
    /** Database column seed_source SqlType(VARCHAR), Length(255,true) */
    val seedSource: Rep[String] = column[String]("seed_source", O.Length(255,varying=true))
    /** Database column store_unit SqlType(VARCHAR), Length(255,true) */
    val storeUnit: Rep[String] = column[String]("store_unit", O.Length(255,varying=true))
    /** Database column original_number SqlType(VARCHAR), Length(255,true) */
    val originalNumber: Rep[String] = column[String]("original_number", O.Length(255,varying=true))
    /** Database column store_unit_2 SqlType(VARCHAR), Length(255,true) */
    val storeUnit2: Rep[String] = column[String]("store_unit_2", O.Length(255,varying=true))
    /** Database column province SqlType(VARCHAR), Length(255,true) */
    val province: Rep[String] = column[String]("province", O.Length(255,varying=true))
    /** Database column sample_kind SqlType(VARCHAR), Length(255,true) */
    val sampleKind: Rep[String] = column[String]("sample_kind", O.Length(255,varying=true))
    /** Database column phenotype SqlType(TEXT) */
    val phenotype: Rep[String] = column[String]("phenotype")
    /** Database column comment SqlType(VARCHAR), Length(255,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(255,varying=true))
    /** Database column photo SqlType(TEXT) */
    val photo: Rep[String] = column[String]("photo")
  }
  /** Collection-like TableQuery object for table ExIntroduction */
  lazy val ExIntroduction = new TableQuery(tag => new ExIntroduction(tag))

  /** Entity class storing rows of table LocalSample
   *  @param number Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param unitNumber Database column unit_number SqlType(VARCHAR), Length(255,true)
   *  @param phenotype Database column phenotype SqlType(LONGTEXT), Length(2147483647,true)
   *  @param comment Database column comment SqlType(LONGTEXT), Length(2147483647,true)
   *  @param photo Database column photo SqlType(TEXT) */
  case class LocalSampleRow(number: String, name: String, unitNumber: String, phenotype: String, comment: String, photo: String)
  /** GetResult implicit for fetching LocalSampleRow objects using plain SQL queries */
  implicit def GetResultLocalSampleRow(implicit e0: GR[String]): GR[LocalSampleRow] = GR{
    prs => import prs._
    LocalSampleRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table local_sample. Objects of this class serve as prototypes for rows in queries. */
  class LocalSample(_tableTag: Tag) extends profile.api.Table[LocalSampleRow](_tableTag, Some("rice_phenotype"), "local_sample") {
    def * = (number, name, unitNumber, phenotype, comment, photo) <> (LocalSampleRow.tupled, LocalSampleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(number), Rep.Some(name), Rep.Some(unitNumber), Rep.Some(phenotype), Rep.Some(comment), Rep.Some(photo)).shaped.<>({r=>import r._; _1.map(_=> LocalSampleRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val number: Rep[String] = column[String]("number", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column unit_number SqlType(VARCHAR), Length(255,true) */
    val unitNumber: Rep[String] = column[String]("unit_number", O.Length(255,varying=true))
    /** Database column phenotype SqlType(LONGTEXT), Length(2147483647,true) */
    val phenotype: Rep[String] = column[String]("phenotype", O.Length(2147483647,varying=true))
    /** Database column comment SqlType(LONGTEXT), Length(2147483647,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(2147483647,varying=true))
    /** Database column photo SqlType(TEXT) */
    val photo: Rep[String] = column[String]("photo")
  }
  /** Collection-like TableQuery object for table LocalSample */
  lazy val LocalSample = new TableQuery(tag => new LocalSample(tag))

  /** Entity class storing rows of table Mode
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param test Database column test SqlType(VARCHAR), Length(255,true) */
  case class ModeRow(id: Int, test: String)
  /** GetResult implicit for fetching ModeRow objects using plain SQL queries */
  implicit def GetResultModeRow(implicit e0: GR[Int], e1: GR[String]): GR[ModeRow] = GR{
    prs => import prs._
    ModeRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table mode. Objects of this class serve as prototypes for rows in queries. */
  class Mode(_tableTag: Tag) extends profile.api.Table[ModeRow](_tableTag, Some("rice_phenotype"), "mode") {
    def * = (id, test) <> (ModeRow.tupled, ModeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(test)).shaped.<>({r=>import r._; _1.map(_=> ModeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column test SqlType(VARCHAR), Length(255,true) */
    val test: Rep[String] = column[String]("test", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Mode */
  lazy val Mode = new TableQuery(tag => new Mode(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true)
   *  @param createTime Database column create_time SqlType(DATETIME) */
  case class UserRow(id: Int, name: String, password: String, createTime: DateTime)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String], e2: GR[DateTime]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String], <<[DateTime]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, Some("rice_phenotype"), "user") {
    def * = (id, name, password, createTime) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(password), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column create_time SqlType(DATETIME) */
    val createTime: Rep[DateTime] = column[DateTime]("create_time")

    /** Uniqueness Index over (name) (database name name_uniq) */
    val index1 = index("name_uniq", name, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))

  /** Entity class storing rows of table UserLimit
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param localSample Database column local_sample SqlType(TEXT)
   *  @param breedSample Database column breed_sample SqlType(TEXT)
   *  @param wildSample Database column wild_sample SqlType(TEXT)
   *  @param exIntroductionSample Database column ex_introduction_sample SqlType(TEXT) */
  case class UserLimitRow(id: Int, localSample: String, breedSample: String, wildSample: String, exIntroductionSample: String)
  /** GetResult implicit for fetching UserLimitRow objects using plain SQL queries */
  implicit def GetResultUserLimitRow(implicit e0: GR[Int], e1: GR[String]): GR[UserLimitRow] = GR{
    prs => import prs._
    UserLimitRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table user_limit. Objects of this class serve as prototypes for rows in queries. */
  class UserLimit(_tableTag: Tag) extends profile.api.Table[UserLimitRow](_tableTag, Some("rice_phenotype"), "user_limit") {
    def * = (id, localSample, breedSample, wildSample, exIntroductionSample) <> (UserLimitRow.tupled, UserLimitRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(localSample), Rep.Some(breedSample), Rep.Some(wildSample), Rep.Some(exIntroductionSample)).shaped.<>({r=>import r._; _1.map(_=> UserLimitRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column local_sample SqlType(TEXT) */
    val localSample: Rep[String] = column[String]("local_sample")
    /** Database column breed_sample SqlType(TEXT) */
    val breedSample: Rep[String] = column[String]("breed_sample")
    /** Database column wild_sample SqlType(TEXT) */
    val wildSample: Rep[String] = column[String]("wild_sample")
    /** Database column ex_introduction_sample SqlType(TEXT) */
    val exIntroductionSample: Rep[String] = column[String]("ex_introduction_sample")
  }
  /** Collection-like TableQuery object for table UserLimit */
  lazy val UserLimit = new TableQuery(tag => new UserLimit(tag))

  /** Entity class storing rows of table WildSample
   *  @param number Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param source Database column source SqlType(VARCHAR), Length(255,true)
   *  @param phenotype Database column phenotype SqlType(TEXT)
   *  @param comment Database column comment SqlType(VARCHAR), Length(255,true)
   *  @param photo Database column photo SqlType(TEXT) */
  case class WildSampleRow(number: String, source: String, phenotype: String, comment: String, photo: String)
  /** GetResult implicit for fetching WildSampleRow objects using plain SQL queries */
  implicit def GetResultWildSampleRow(implicit e0: GR[String]): GR[WildSampleRow] = GR{
    prs => import prs._
    WildSampleRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table wild_sample. Objects of this class serve as prototypes for rows in queries. */
  class WildSample(_tableTag: Tag) extends profile.api.Table[WildSampleRow](_tableTag, Some("rice_phenotype"), "wild_sample") {
    def * = (number, source, phenotype, comment, photo) <> (WildSampleRow.tupled, WildSampleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(number), Rep.Some(source), Rep.Some(phenotype), Rep.Some(comment), Rep.Some(photo)).shaped.<>({r=>import r._; _1.map(_=> WildSampleRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column number SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val number: Rep[String] = column[String]("number", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column source SqlType(VARCHAR), Length(255,true) */
    val source: Rep[String] = column[String]("source", O.Length(255,varying=true))
    /** Database column phenotype SqlType(TEXT) */
    val phenotype: Rep[String] = column[String]("phenotype")
    /** Database column comment SqlType(VARCHAR), Length(255,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(255,varying=true))
    /** Database column photo SqlType(TEXT) */
    val photo: Rep[String] = column[String]("photo")
  }
  /** Collection-like TableQuery object for table WildSample */
  lazy val WildSample = new TableQuery(tag => new WildSample(tag))
}
