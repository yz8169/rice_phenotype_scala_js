package utils

import java.io.{File, FileInputStream, FileOutputStream}
import java.lang.reflect.Field
import java.text.SimpleDateFormat

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{Cell, CellType, DateUtil}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.sys.process.Process
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/**
 * Created by yz on 2017/6/16.
 */
object Utils {

  val errorClass = "error"
  val successClass = "text-success"

  val projectName = "rice_phenotype"
  val dbName = "rice_phenotype_database"
  val windowsPath = s"D:\\${dbName}"
  val playPath = new File("../").getAbsolutePath
  val linuxPath = playPath + s"/${dbName}"

  val isWindows = {
    if (new File(windowsPath).exists()) true else false
  }

  val path = {
    if (new File(windowsPath).exists()) windowsPath else linuxPath
  }

  val appendixDir = new File(path, "appendix")
  val breedSampleADir = new File(appendixDir, "breed_sample")
  val localSampleADir = new File(appendixDir, "local_sample")
  val wildSampleADir = new File(appendixDir, "wild_sample")
  val exIntroductionADir = new File(appendixDir, "ex_introduction")

  def result2Future(result: Result) = {
    Future.successful(result)
  }


  var error: String = _
  val sep = "__SEP__"
  val binPath = new File(path, "bin")
  val dataPath = new File(path, "data")
  val localSamplePath = new File(dataPath, "local_sample")
  val goPy = {
    val path = "C:\\Python\\python.exe"
    if (new File(path).exists()) path else "python"
  }

  def createDir(dir: File) = {
    FileUtils.forceMkdir(dir)
  }

  val rPath = {
    val rPath = s"D:\\workspaceForIDEA\\${projectName}\\rScripts"
    val linuxRPath = linuxPath + "/rScripts"
    if (new File(rPath).exists()) rPath else linuxRPath
  }
  val pyPath = {
    val path = s"D:\\workspaceForIDEA\\${projectName}\\pythons"
    val linuxPyPath = linuxPath + "/pyScripts"
    if (new File(path).exists()) path else linuxPyPath
  }
  val toolsPath = "/root/projects/tools"
  val samtoolsPath = if (isWindows) new File(Utils.binPath, "samtools-0.1.19/samtools").getAbsolutePath else
    s"${toolsPath}/samtools-1.9/samtools"
  val windowsTestDir = new File("G:\\temp")
  val linuxTestDir = new File(playPath, "workspace")
  val testDir = if (windowsTestDir.exists()) windowsTestDir else linuxTestDir

  def getTime(startTime: Long) = {
    val endTime = System.currentTimeMillis()
    (endTime - startTime) / 1000.0
  }

  val phylotreeCss =
    """
      |<style>
      |.tree-selection-brush .extent {
      |    fill-opacity: .05;
      |    stroke: #fff;
      |    shape-rendering: crispEdges;
      |}
      |
      |.tree-scale-bar text {
      |  font: sans-serif;
      |}
      |
      |.tree-scale-bar line,
      |.tree-scale-bar path {
      |  fill: none;
      |  stroke: #000;
      |  shape-rendering: crispEdges;
      |}
      |
      |.node circle, .node ellipse, .node rect {
      |fill: steelblue;
      |stroke: black;
      |stroke-width: 0.5px;
      |}
      |
      |.internal-node circle, .internal-node ellipse, .internal-node rect{
      |fill: #CCC;
      |stroke: black;
      |stroke-width: 0.5px;
      |}
      |
      |.node {
      |font: 10px sans-serif;
      |}
      |
      |.node-selected {
      |fill: #f00 !important;
      |}
      |
      |.node-collapsed circle, .node-collapsed ellipse, .node-collapsed rect{
      |fill: black !important;
      |}
      |
      |.node-tagged {
      |fill: #00f;
      |}
      |
      |.branch {
      |fill: none;
      |stroke: #999;
      |stroke-width: 2px;
      |}
      |
      |.clade {
      |fill: #1f77b4;
      |stroke: #444;
      |stroke-width: 2px;
      |opacity: 0.5;
      |}
      |
      |.branch-selected {
      |stroke: #f00 !important;
      |stroke-width: 3px;
      |}
      |
      |.branch-tagged {
      |stroke: #00f;
      |stroke-dasharray: 10,5;
      |stroke-width: 2px;
      |}
      |
      |.branch-tracer {
      |stroke: #bbb;
      |stroke-dasharray: 3,4;
      |stroke-width: 1px;
      |}
      |
      |
      |.branch-multiple {
      |stroke-dasharray: 5, 5, 1, 5;
      |stroke-width: 3px;
      |}
      |
      |.branch:hover {
      |stroke-width: 10px;
      |}
      |
      |.internal-node circle:hover, .internal-node ellipse:hover, .internal-node rect:hover {
      |fill: black;
      |stroke: #CCC;
      |}
      |
      |.tree-widget {
      |}
      |</style>
    """.stripMargin

  def callScript(tmpDir: File, shBuffer: ArrayBuffer[String]) = {
    val execCommand = new ExecCommand
    val runFile = if (Utils.isWindows) {
      new File(tmpDir, "run.bat")
    } else {
      new File(tmpDir, "run.sh")
    }
    FileUtils.writeLines(runFile, shBuffer.asJava)
    val shCommand = runFile.getAbsolutePath
    if (Utils.isWindows) {
      execCommand.exec(shCommand, tmpDir)
    } else {
      val useCommand = "chmod +x " + runFile.getAbsolutePath
      val dos2Unix = "dos2unix " + runFile.getAbsolutePath
      execCommand.exec(dos2Unix, useCommand, shCommand, tmpDir)
    }
    execCommand
  }

  def str2Map(str: String) = {
    if (StringUtils.isBlank(str)) Map[String, String]() else Json.parse(str).as[Map[String, String]]
  }

  def str2Json(str: String) = {
    if (StringUtils.isEmpty(str)) {
      Json.obj()
    } else {
      Json.parse(str).as[JsObject]
    }

  }

  def getJsonByT[T](y: T) = {
    val map = y.getClass.getDeclaredFields.toBuffer.map { x: Field =>
      x.setAccessible(true)
      val kind = x.get(y)
      val value = getValue(kind, "")
      (x.getName, value)
    }.init.toMap
    Json.toJson(map)
  }


  def getArrayByTs[T](x: Seq[T]) = {
    x.map { y =>
      getMapByT(y)
    }
  }

  def getArrayByTs[T](x: Seq[T], jsonField: String) = {
    x.map { y =>
      getMapByT(y, jsonField)
    }
  }

  def getArrayByTs[A:ClassTag, B:ClassTag](x: Seq[(A, B)]) = {
    x.map { case (t1, t2) =>
      val map1 = getMapByT(t1)
      val map2 = getMapByT(t2)
      map1 ++ map2
    }
  }


  def getMapByT[T](t: T, jsonField: String) = {
    t.getClass.getDeclaredFields.toBuffer.flatMap { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val filedName = x.getName
      if (filedName == jsonField) {
        Utils.str2Map(kind.toString)
      } else {
        val value = getValue(kind)
        Map(filedName -> value)
      }
    }.init.toMap
  }

  def getMapByT[T](t: T) = {
    t.getClass.getDeclaredFields.toBuffer.map { x: Field =>
      x.setAccessible(true)
      val kind = x.get(t)
      val value = getValue(kind)
      (x.getName, value)
    }.init.toMap
  }

  def getValue[T](kind: T, noneMessage: String = "暂无"): String = {
    kind match {
      case x if x.isInstanceOf[DateTime] => val time = x.asInstanceOf[DateTime]
        time.toString("yyyy-MM-dd HH:mm:ss")
      case x if x.isInstanceOf[Option[T]] => val option = x.asInstanceOf[Option[T]]
        if (option.isDefined) getValue(option.get, noneMessage) else noneMessage
      case _ => kind.toString
    }
  }


  def xlsx2Lines(xlsxFile: File) = {
    val is = new FileInputStream(xlsxFile.getAbsolutePath)
    val xssfWorkbook = new XSSFWorkbook(is)
    val xssfSheet = xssfWorkbook.getSheetAt(0)
    val lines = ArrayBuffer[String]()
    for (i <- 0 to xssfSheet.getLastRowNum) {
      val columns = ArrayBuffer[String]()
      val xssfRow = xssfSheet.getRow(i)
      for (j <- 0 until xssfSheet.getRow(0).getLastCellNum) {
        val cell = xssfRow.getCell(j)
        var value = ""
        if (cell != null) {
          cell.getCellTypeEnum match {
            case CellType.STRING =>
              value = cell.getStringCellValue
            case CellType.NUMERIC =>
              if (DateUtil.isCellDateFormatted(cell)) {
                val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
                value = dateFormat.format(cell.getDateCellValue)
              } else {
                value = cell.getRawValue
              }
            case CellType.BLANK =>
              value = ""
            case _ =>
              value = ""
          }
        }

        columns += value
      }
      val line = columns.mkString("\t")
      lines += line
    }
    xssfWorkbook.close()
    lines.filter(StringUtils.isNotBlank(_))
  }


  def execFuture[T](f: Future[T]): T = {
    Await.result(f, Duration.Inf)
  }

  def file2Lines(file: File) = {
    FileUtils.readLines(file).asScala
  }


  def pyCluster(file: File, names: String, height: Int) = {
    val path = file.getParent.replaceAll("\\\\", "/")
    val fileName = file.getName
    val pyStr =
      s"""
         |# coding=UTF-8
         |import plotly
         |import plotly.figure_factory as ff
         |from itertools import islice
         |import numpy as np
         |import os
         |os.chdir("$path")
         |data_lines = []
         |input_file = open("$fileName")
         |for line in islice(input_file, 1, None):
         |    line = line.strip("\\n").split("\\t")
         |    del line[0]
         |    for index in range(len(line)):
         |        line[index]=float(line[index])
         |    data_lines.append(line)
         |input_file.close()
         |X = np.mat(data_lines)
         |names=$names
         |fig = ff.create_dendrogram(X, orientation='left',labels=names)
         |fig['layout'].update({'width':1160, 'height':$height,'yaxis':{'fixedrange':True},'xaxis':{'fixedrange':True}})
         |fig['data'].update({'name':""})
         |config={}
         |config['displayModeBar']=False
         |a=plotly.offline.plot(fig, filename='simple_dendrogram.html',output_type='div',include_plotlyjs=False,show_link=False,config=config)
         |f=open('div.txt','w')
         |f.write(a)
         |f.close()
      """.stripMargin
    val pyFile = new File(path, "tmp.py")
    FileUtils.writeStringToFile(pyFile, pyStr)
    Process("python  " + pyFile.getAbsolutePath)
  }

  def dataDuplicate(dataFile: File) = {
    val dataLines = FileUtils.readLines(dataFile).asScala
    val newHeaders = dataLines.head.split("\t")
    val newLines = dataLines.drop(1).map { x =>
      val columns = x.split("\t").toBuffer
      (columns.head, columns.tail)
    }.groupBy(_._2).mapValues(x => x.map(_._1).mkString(",")).map { case (x, y) => (y, x) }
    val newBuffer = mutable.Buffer[mutable.Buffer[String]]()
    newBuffer += newHeaders.toBuffer
    newBuffer ++= newLines.map {
      case (x, y) => val line = mutable.Buffer[String]()
        line += x
        line ++= y
    }
    FileUtils.writeLines(dataFile, newBuffer.map(_.mkString("\t")).asJava)
  }

  def deleteDirectory(direcotry: File) = {
    try {
      FileUtils.deleteDirectory(direcotry)
    } catch {
      case _ =>
    }
  }

  def txt2Excel(txtFile: File, xlsxFile: File) = {
    val buffer = FileUtils.readLines(txtFile).asScala
    val outputWorkbook = new XSSFWorkbook
    val outputSheet = outputWorkbook.createSheet("Sheet1")
    buffer.zipWithIndex.foreach { case (line, index) =>
      val row = outputSheet.createRow(index)
      line.split("\t").zipWithIndex.foreach { case (column, i) =>
        val cell = row.createCell(i)
        cell.setCellValue(column)
      }
    }
    val fileOutputStream = new FileOutputStream(xlsxFile)
    outputWorkbook.write(fileOutputStream)
    fileOutputStream.close()
    outputWorkbook.close()
  }

  def xlsx2Txt(xlsxFile: File, txtFile: File) = {
    val lines = xlsx2Lines(xlsxFile)
    lines2txt(txtFile, lines)
  }

  def isDouble(value: String): Boolean = {
    try {
      value.toDouble
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def lines2txt(txtFile: File, lines: mutable.Buffer[String]) = {
    FileUtils.writeLines(txtFile, lines.asJava)
  }

  def lines2Xlsx(lines: mutable.Buffer[String], xlsxFile: File) = {
    val outputWorkbook = new XSSFWorkbook()
    val outputSheet = outputWorkbook.createSheet("Sheet1")
    for (i <- 0 until lines.size) {
      val outputEachRow = outputSheet.createRow(i)
      val line = lines(i)
      val columns = line.split("\t")
      for (j <- 0 until columns.size) {
        var cell = outputEachRow.createCell(j)
        if (Utils.isDouble(columns(j))) {
          cell.setCellValue(columns(j).toDouble)
        } else {
          cell.setCellValue(columns(j))
        }

      }
    }

    val fileOutputStream = new FileOutputStream(xlsxFile)
    outputWorkbook.write(fileOutputStream)
    fileOutputStream.close()
    outputWorkbook.close()
  }

  def txt2Lines(file: File) = {
    FileUtils.readLines(file).asScala
  }

  def isInt(value: String): Boolean = {
    try {
      val double = value.toDouble
      double == double.toInt
    } catch {
      case _: Exception =>
        false
    }
  }

  def isInteger(value: String): Boolean = {
    try {
      value.toInt
    } catch {
      case _: Exception =>
        return false
    }
    true
  }


}
