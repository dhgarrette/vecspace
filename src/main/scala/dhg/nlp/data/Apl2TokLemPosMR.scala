package dhg.nlp.data

import com.utcompling.tacc.scalding.ScaldingJob
import com.twitter.scalding._
import dhg.nlp.data._
import dhg.util.CollectionUtil._

object Apl2TokLemPosMR extends ScaldingJob {
  def jobClass = classOf[Apl2TokLemPosMRClass]
}

class Apl2TokLemPosMRClass(args: Args) extends Job(args) {
  val (inputAplFile, outputTlpFile) =
    args.positional match {
      case Seq(inputAplFile, outputFile) => (inputAplFile, outputFile)
    }

  TypedPipe.from(TextLine(inputAplFile))
    .map { articleLine =>
      val Vector(id, typ, paragraphs @ _*) = articleLine.split("\t").toVector
      Giga2TokLemPos.toTlpString(id, typ, paragraphs.flatMap(Giga2TokLemPos.annotator.apply))
    }
    .write(TypedTsv[String](outputTlpFile))
}
