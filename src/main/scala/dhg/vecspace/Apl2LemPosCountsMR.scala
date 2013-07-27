package dhg.vecspace

import com.utcompling.tacc.scalding.ScaldingJob
import com.twitter.scalding._
import dhg.nlp.data._
import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import dhg.nlp.data.AnnotatedData.AnnotatedToken
import dhg.nlp.data.Giga2TokLemPos.CleanTok
import dhg.nlp.data.Giga2TokLemPos.CleanPos

object Apl2LemPosCountsMR extends ScaldingJob {
  def jobClass = classOf[Apl2LemPosCountsMRClass]

}

class Apl2LemPosCountsMRClass(args: Args) extends Job(args) {
  val (inputAplFile, outputTlpFile) =
    args.positional match {
      case Seq(inputAplFile, outputFile) => (inputAplFile, outputFile)
      case _ => sys.error("bad args: " + args.m)
    }

  TypedPipe.from(TextLine(inputAplFile))
    .map(_.trim)
    .filter(_.nonEmpty)
    .flatMap { articleLine =>
      val Vector(id, typ, paragraphs @ _*) = articleLine.split("\t").toVector
      paragraphs.toVector.par
        .flatMap(paragraph =>
          Giga2TokLemPos.annotator.apply(paragraph)
            .flatMap(_.collect {
              case AnnotatedToken(w, CleanTok(l), CleanPos(p), _) if Idf.ValidLemma(l) && !Idf.InvalidPos(p) => ("%s\t%s".format(l, p), 1)
            })).seq
    }
    .group
    .reduce(_ + _)
    .map { case (lp, c) => "%s\t%s".format(lp, c) }
    .write(TypedTsv[String](outputTlpFile))
}
