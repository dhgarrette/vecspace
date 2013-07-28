package dhg.vecspace

import com.utcompling.tacc.scalding.ScaldingJob
import com.twitter.scalding._
import dhg.nlp.data._
import dhg.util.CollectionUtil._
import dhg.util.FileUtil._

object Apl2TokLemPosMR extends ScaldingJob {
  def jobClass = classOf[Apl2TokLemPosMRClass]
}

class Apl2TokLemPosMRClass(args: Args) extends Job(args) {
  val (inputAplFile, outputTlpFile) =
    args.positional match {
      case Seq(inputAplFile, outputFile) => (inputAplFile, outputFile)
      case _ => sys.error("bad args: " + args.m)
    }

  TypedPipe.from(TextLine(inputAplFile))
    .map(_.trim)
    .filter(_.nonEmpty)
    .map { articleLine =>
      val Vector(id, typ, paragraphs @ _*) = articleLine.split("\t").toVector
      Giga2TokLemPos.toTlpString(id, typ, paragraphs.toVector.par.flatMap(Giga2TokLemPos.annotator.apply).seq)
    }
    .write(TypedTsv[String](outputTlpFile))
}
