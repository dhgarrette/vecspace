package dhg.nlp.data

import scala.collection.generic.CanBuildFrom
import scala.collection.GenTraversableLike
import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import dhg.util.Arm._
import dhg.util.CollectionUtil.KeepDelimiter._
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.io.FileInputStream

object Giga2APL {

  val DocHeadRe = """<DOC +id="(.+)" +type="(.+)" *>""".r

  val EosRe = """X*END OF STORY""".r

  def readArticles(inputFile: java.io.File) = {
    val lines =
      SelfClosingBufferedReaderIterator(GzFileBufferedReader(inputFile))
        .map(_.trim)
        .filter(_ != "(STORY CAN END HERE. OPTIONAL 2ND TAKE FOLLOWS.)")
        .filterNot(EosRe.pattern.matcher(_).matches)

    val articles =
      lines.splitWhere((line: String) => line.startsWith("<DOC "), KeepDelimiterAsFirst)
        .filter(_.nonEmpty)

    for (
      article <- articles;
      DocHeadRe(id, typ) = article.head;
      if Set("story").contains(typ)
    ) yield {
      val paragraphs =
        article
          .dropWhile(_ != "<TEXT>").drop(1)
          .dropRightWhile(_ != "</TEXT>").dropRight(1)
          .splitWhere(Set("<P>", "</P>"))
          .flatMap(_.split(""))
          .filter(_.nonEmpty)
          .map(_.mkString(" "))
          .toVector
      (id, typ, paragraphs)
    }
  }

  def toAplString(id: String, typ: String, paragraphs: Vector[String]) = {
    val sb = new StringBuilder
    sb ++= id.replaceAll("\\s+", "")
    sb ++= "\t"
    sb ++= typ.replaceAll("\\s+", "")
    sb ++= "\t"
    sb ++= paragraphs.mkString("\t")
    sb.result
  }

  def main(args: Array[String]): Unit = {
    val (inputDir, outputFilename) =
      args.toList match {
        case Seq(inputDir, outputFilename) => (inputDir, outputFilename)
      }
    val GzFilenameRe = "(.*)\\.gz".r

    println("Reading: %s".format(inputDir))
    println("Writing: %s".format(outputFilename))

    writeUsing(File(outputFilename)) { w =>
      for (inputFile <- File(inputDir).ls(GzFilenameRe)) {
        val GzFilenameRe(filename) = inputFile.name
        print("Handling: " + filename + " ...")
        val startTime = System.currentTimeMillis()
        for ((id, typ, paragraphs) <- readArticles(inputFile)) {
          w.wl(toAplString(id, typ, paragraphs))
        }
        println("done (" + ((System.currentTimeMillis() - startTime) / 1000.0) + " sec)")
      }
    }
  }

}
