package dhg.vecspace

import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import com.twitter.scalding._
import com.utcompling.tacc.scalding.ScaldingJob

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
      val Vector(l, p, c) = countLine.split("\t").toVector
      ("%s\t%s".format(l, p), c.toInt)
    }
    .group
    .reduce(_ + _)
    .filter { case (lp, c) => c >= minCount }
    .map { case (lp, c) => "%s\t%s".format(lp, c) }
    .write(TypedTsv[String](outputIdfFile))

}
