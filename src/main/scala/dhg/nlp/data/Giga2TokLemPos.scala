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
import dhg.nlp.data.AnnotatedData._

object Giga2TokLemPos {

  val annotator = new StanfordAnnotator()

  def readAplFile(inputFile: java.io.File) = {
    inputFile.readLines
      .map { line =>
        val Array(id, typ, paragraphs @ _*) = line.split("\t")
        (id, typ, paragraphs.toVector)
      }
  }

  def tokLemTagArticle(id: String, typ: String, paragraphs: Vector[String]) = {
    val annotatedDoc = annotator(paragraphs)
  }

  def toTlpString(id: String, typ: String, sentences: Seq[AnnotatedSentence]) = {
    val sentenceStrings = sentences.map(s => s.map { case AnnotatedToken(CleanTok(w), CleanTok(l), p, _) => "%s|%s|%s".format(w, l, p) }.mkString(" "))

    val sb = new StringBuilder
    sb ++= id.replaceAll("\\s+", "")
    sb ++= "\t"
    sb ++= typ.replaceAll("\\s+", "")
    sb ++= "\t"
    sb ++= sentenceStrings.mkString("\t")
    sb.result
  }

  def main(args: Array[String]): Unit = {
    val (inputDir, inputFilenamePattern, outputDir) =
      args.toList match {
        case Seq(inputDir, inputFilenamePattern, outputDir) => (inputDir, inputFilenamePattern, outputDir)
        case Seq(inputDir, outputDir) => (inputDir, """.+""", outputDir)
      }
    val GzFilenameRe = ("(" + inputFilenamePattern + ")\\.gz").r

    println("Reading: %s/%s".format(inputDir, inputFilenamePattern))
    println("Writing: %s".format(outputDir))

    for (inputFile <- File(inputDir).ls(GzFilenameRe)) {
      val GzFilenameRe(filename) = inputFile.name
      print("Handling: " + filename + " ...")
      val startTime = System.currentTimeMillis()
      writeUsing(File(outputDir, filename + ".tlp")) { w =>
        for ((id, typ, paragraphs) <- Giga2APL.readArticles(inputFile)) {
          w.wl(toTlpString(id, typ, annotator(paragraphs)))
        }
      }
      println("done (" + ((System.currentTimeMillis() - startTime) / 1000.0) + " sec)")
    }
  }

  object CleanTok {
    def unapply(t: String): Option[String] = Some(t match {
      case "|" => "-VERTBAR-"
      case _ => t
    })
  }

}
