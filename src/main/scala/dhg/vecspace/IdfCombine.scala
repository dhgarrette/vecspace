package dhg.vecspace

import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import com.twitter.scalding._
import com.utcompling.tacc.scalding.ScaldingJob
import dhg.nlp.data.Giga2TokLemPos.CleanTok
import dhg.nlp.data.Giga2TokLemPos.CleanPos
import dhg.vecspace.Idf.ValidLemma
import dhg.vecspace.Idf.InvalidPos

object IdfCombine extends ScaldingJob {
  def jobClass = classOf[IdfCombineClass]
}

class IdfCombineClass(args: Args) extends Job(args) {
  val (inputIdfFile, minCount, outputIdfFile) =
    args.positional match {
      case Seq(inputIdfFile, minCount, outputIdfFile) => (inputIdfFile, minCount.toInt, outputIdfFile)
      case Seq(inputIdfFile, outputIdfFile) => (inputIdfFile, 1, outputIdfFile)
    }

  TypedPipe.from(TextLine(inputIdfFile))
    .map { countLine =>
      val Vector(CleanTok(l), CleanPos(p), c) = countLine.split("\t").toVector
      (l, p, c.toInt)
    }
    .filter { case (l, p, c) => ValidLemma(l) && !InvalidPos(p) }
    .map { case (l, p, c) => ("%s\t%s".format(l, p), c) }
    .group
    .reduce(_ + _)
    .filter { case (lp, c) => c >= minCount }
    .map { case (lp, c) => "%s\t%s".format(lp, c) }
    .write(TypedTsv[String](outputIdfFile))

}
