package tool

import java.io.File
import java.nio.file.Files

import dao.ModeDao
import javax.inject.Inject
import utils.Utils

import scala.collection.mutable.ArrayBuffer

/**
  * Created by yz on 2019/1/18
  */
class Tool @Inject()(modeDao: ModeDao) {

  def createTempDirectory(prefix: String) = {
    if (isTestMode) Utils.testDir else Files.createTempDirectory(prefix).toFile
  }

  def isTestMode = {
    val mode = Utils.execFuture(modeDao.select)
    if (mode.test == "t") true else false
  }

  def deleteDirectory(direcotry: File) = {
    if (!isTestMode) Utils.deleteDirectory(direcotry)
  }

}

object Tool {




}
