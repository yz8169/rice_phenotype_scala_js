package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * Created by yz on 2019/1/11
  */
class IndexController @Inject()(cc:ControllerComponents) extends AbstractController(cc){

  def toIndex = Action {implicit request=>
    Ok(views.html.index())
  }


}
