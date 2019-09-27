package controllers

import java.io.File

import javax.inject.Inject
import models.Tables.LocalSampleRow
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.Utils

import scala.collection.mutable.ArrayBuffer
import dao._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by yz on 2019/1/18
  */
class TestController @Inject()(cc: ControllerComponents, localSampleDao: LocalSampleDao) extends AbstractController(cc) {

  def test = Action.async { implicit request =>
    val phenotypeNames = ArrayBuffer("籼粳", "早中晚", "水陆", "粘糯", "播种期", "成熟期", "全生育日数", "叶片色", "叶鞘色", "叶片茸毛",
      "剑叶长", "剑叶角度", "茎集散", "节间色", "倒伏性", "株高", "有效穗数", "穗颈长短", "柱头色", "柱头外露", "穗长",
      "每穗粒数", "结实率", "芒长", "粒形", "粒长短", "护颖色", "护颖长短", "颖尖色", "颖壳色", "颖毛", "千粒重", "落粒性",
      "种皮色", "糙米率", "精米率", "垩白大小", "垩白率", "米的香味", "米质", "蛋白质", "赖氨酸", "总淀粉",
      "支链淀粉", "直链淀粉", "糊化温度", "胶稠度", "苗瘟", "叶瘟", "穗瘟", "白叶枯", "细菌性条斑", "纹枯",
      "稻瘿蚊", "三化螟", "白背飞虱", "褐稻虱", "芽期耐寒", "苗期耐寒", "花期耐寒", "苗期耐旱", "后期抗旱", "耐涝性",
      "苗期耐盐", "根系泌氧力")

    val parent = new File("E:\\rice_phenotype\\test")
    val file = new File(parent, "地方稻种 - 副本.xlsx")
    val lines = Utils.xlsx2Lines(file)
    val headers = lines.head.split("\t")
    val rows = lines.drop(1).map { line =>
      val columns = line.split("\t")
      val map = headers.zip(columns).toMap
      val phenotypeMap = map.filter { case (key, value) =>
        phenotypeNames.contains(key)
      }
      LocalSampleRow(map("统一编号"), map("品种名称"), map("单位编号"), Json.toJson(phenotypeMap).toString(),
        map.getOrElse("备注", ""),"")
    }


    localSampleDao.insertOrUpdates(rows).map { x =>
      Ok("success!")
    }

  }


}
