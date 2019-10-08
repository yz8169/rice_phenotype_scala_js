package controllers

import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import shared.Shared
import utils.Utils
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by Administrator on 2019/10/8
 */
class ToolController @Inject()(cc: ControllerComponents,localSampleDao:LocalSampleDao,breedSampleDao:BreedSampleDao) extends AbstractController(cc){





}
