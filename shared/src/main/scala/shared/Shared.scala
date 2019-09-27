package shared

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by yz on 2019/3/6
  */
object Shared {

  val localSamplePhenotypeNames = ArrayBuffer("籼粳", "早中晚", "水陆", "粘糯", "全生育日数", "叶片色", "叶鞘色", "叶片茸毛",
    "剑叶长", "剑叶角度", "茎集散", "节间色", "倒伏性", "株高", "有效穗数", "穗颈长短", "柱头色", "柱头外露", "穗长", "每穗粒数",
    "结实率", "芒长", "粒形", "粒长短", "护颖色", "护颖长短", "颖尖色", "颖壳色", "颖毛", "千粒重", "落粒性", "种皮色", "糙米率",
    "精米率", "垩白大小", "垩白率", "米的香味", "米质", "蛋白质", "赖氨酸", "总淀粉", "支链淀粉", "直链淀粉", "糊化温度", "胶稠度",
    "苗瘟", "叶瘟", "穗瘟", "白叶枯", "细菌性条斑", "纹枯", "稻瘿蚊", "三化螟", "白背飞虱", "褐稻虱", "耐寒", "耐旱", "耐涝",
    "耐盐", "根系泌氧力")

  val numberLocalSamples = ArrayBuffer("全生育日数", "株高", "穗颈长短", "穗长", "每穗粒数", "结实率", "千粒重", "糙米率",
    "精米率", "蛋白质", "赖氨酸", "总淀粉", "支链淀粉", "直链淀粉", "糊化温度", "胶稠度", "苗瘟")

  val qualityLocalSamples = ArrayBuffer("籼粳", "早中晚", "水陆", "粘糯", "叶片色", "叶鞘色", "叶片茸毛", "剑叶长", "剑叶角度",
    "茎集散", "节间色", "倒伏性", "柱头色", "柱头外露", "芒长", "粒形", "粒长短", "护颖色", "护颖长短", "颖尖色", "颖壳色", "颖毛",
    "落粒性", "种皮色", "垩白大小", "米的香味", "米质")

  val localSampleMap = {
    val t = localSamplePhenotypeNames.filter { x => numberLocalSamples.contains(x) || qualityLocalSamples.contains(x) }.map { x =>
      val desc = if (numberLocalSamples.contains(x)) "数量" else "质量"
      (x, desc)
    }
    mutable.LinkedHashMap(t: _*)
  }

  val localSelectPhenotypes = Shared.localSampleMap.keys.toList

  val breedSamplePhenotypeNames = ArrayBuffer("粘糯", "籼粳", "颖毛", "米味", "米色", "颖尖弯直", "落粒性", "每穗实粒数",
    "每穗秕粒数", "每穗总粒数", "结实率", "千粒重", "有效穗", "株高", "穗长", "剑叶长/cm", "剑叶宽/mm", "抽穗期", "粒长",
    "粒宽", "粒厚", "护颖长", "护颖宽", "米长", "米宽", "米厚", "苗瘟", "叶瘟", "穗瘟", "白叶枯", "细菌性条斑", "纹枯",
    "稻瘿蚊", "三化螟", "白背飞虱", "褐稻虱", "耐寒", "耐旱", "耐涝", "耐盐", "根系泌氧力")

  val numberBreedSamples = ArrayBuffer("每穗实粒数", "每穗秕粒数", "每穗总粒数", "结实率", "千粒重", "有效穗",
    "株高", "穗长", "剑叶长/cm", "剑叶宽/mm", "抽穗期", "粒长", "粒宽", "粒厚", "护颖长", "护颖宽", "米长", "米宽", "米厚")

  val qualityBreedSamples = ArrayBuffer("粘糯", "籼粳", "颖毛", "米味", "米色", "颖尖弯直", "落粒性")

  val breedSampleMap = {
    val t = breedSamplePhenotypeNames.filter { x => numberBreedSamples.contains(x) || qualityBreedSamples.contains(x) }.
      map { x =>
        val desc = if (numberBreedSamples.contains(x)) "数量" else "质量"
        (x, desc)
      }
    mutable.LinkedHashMap(t: _*)
  }

  val breedSelectPhenotypes = Shared.breedSampleMap.keys.toList

  val wildSamplePhenotypeNames = ArrayBuffer("生长习性gh", "基部叶鞘色", "成熟期内外颖色cm", "柱头色sgc", "种皮颜色cc",
    "外观品质", "花药长al", "谷粒长gl", "谷粒宽gw", "谷粒长宽比", "百粒重gw", "叶瘟", "穗瘟", "白叶枯", "细菌性条斑", "纹枯",
    "稻瘿蚊", "三化螟", "白背飞虱", "褐飞虱", "耐寒", "耐旱", "耐涝", "耐盐")

  val numberWildSamples = ArrayBuffer("花药长al", "谷粒长gl", "谷粒宽gw", "谷粒长宽比", "百粒重gw")

  val qualityWildSamples = ArrayBuffer("生长习性gh", "基部叶鞘色", "成熟期内外颖色cm", "柱头色sgc", "种皮颜色cc", "外观品质")

  val wildSampleMap = {
    val t = wildSamplePhenotypeNames.filter { x => numberWildSamples.contains(x) || qualityWildSamples.contains(x) }.
      map { x =>
        val desc = if (numberWildSamples.contains(x)) "数量" else "质量"
        (x, desc)
      }
    mutable.LinkedHashMap(t: _*)
  }

  val wildSelectPhenotypes = Shared.wildSampleMap.keys.toList

  val exIntroductionPhenotypeNames =ArrayBuffer("籼粳", "早中晚", "水陆", "粘糯", "米色", "芒长", "粒形状", "粒长度",
    "颖尖色", "颖壳色", "株高", "出穗期", "苗瘟", "白叶枯", "纹枯病", "褐稻虱", "白背飞虱", "芽期耐寒", "苗期耐旱", "耐盐",
    "糙米率", "精米率", "垩白率", "蛋白质", "赖氨酸", "总淀粉", "直链淀粉", "支链淀粉", "糊化温度", "胶稠度")

  val numberExIntroductions = ArrayBuffer("株高", "出穗期",  "耐盐", "糙米率", "精米率", "垩白率", "蛋白质", "赖氨酸",
    "总淀粉", "直链淀粉", "支链淀粉", "糊化温度", "胶稠度")


  val qualityExIntroductions = ArrayBuffer("籼粳", "早中晚", "水陆", "粘糯", "米色", "芒长", "粒形状", "粒长度",
    "颖尖色", "颖壳色", "苗瘟", "白叶枯", "纹枯病", "褐稻虱", "白背飞虱", "芽期耐寒", "苗期耐旱")

  val exIntroductionMap = {
    val t = exIntroductionPhenotypeNames.filter { x => numberExIntroductions.contains(x) ||
      qualityExIntroductions.contains(x) }.
      map { x =>
        val desc = if (numberExIntroductions.contains(x)) "数量" else "质量"
        (x, desc)
      }
    mutable.LinkedHashMap(t: _*)
  }

  val exSelectPhenotypes = Shared.exIntroductionMap.keys.toList


  def getTime(startTime: Long) = {
    val endTime = System.currentTimeMillis()
    (endTime - startTime) / 1000.0
  }


}
