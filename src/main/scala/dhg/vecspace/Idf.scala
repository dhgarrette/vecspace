package dhg.vecspace

import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import com.twitter.scalding._
import com.utcompling.tacc.scalding.ScaldingJob
import dhg.nlp.data.Giga2TokLemPos.CleanTok
import dhg.nlp.data.Giga2TokLemPos.CleanPos
import dhg.vecspace.Idf.ValidLemma
import dhg.vecspace.Idf.InvalidPos

object Idf extends ScaldingJob {
  def jobClass = classOf[IdfClass]

  val HasLetter = ".*[A-Za-z].*".r
  val LineRe = ".*----.*".r
  val Punctuation = (w: String) => Set(".", ",", "``", "''", "'", "`", "--", ":", ";", "(", ")", "[", "]", "{", "}", "-RRB-", "-LRB-", "?", "!", "-RCB-", "-LCB-", "-RSB-", "-LSB-", "...", "-", "_", "-VERTBAR-")(w.toUpperCase)
  val ValidLemma = (w: String) =>
    !Stopwords(w.toLowerCase) &&
      HasLetter.pattern.matcher(w).matches &&
      !Punctuation(w) &&
      !LineRe.pattern.matcher(w).matches
  val InvalidPos = (p: String) => Set(".", ":", ",", "``", "''", "-LRB-", "-RRB-", "#", "CC", "CD", "DT", "EX", "IN", "LS", "MD", "PDT", "POS", "PRP", "PRP$", "RP", "SYM", "TO", "UH", "WDT", "WP", "WP$", "WRB").map(_.take(2)).contains(p.take(2))
}

class IdfClass(args: Args) extends Job(args) {
  val (inputTlpFile, minCount, outputIdfFile) =
    args.positional match {
      case Seq(inputTlpFile, minCount, outputIdfFile) => (inputTlpFile, minCount.toInt, outputIdfFile)
      case Seq(inputTlpFile, outputIdfFile) => (inputTlpFile, 1, outputIdfFile)
    }

  TypedPipe.from(TextLine(inputTlpFile))
    .flatMap { articleLine =>
      val Vector(id, typ, sentences @ _*) = articleLine.split("\t").toVector
      sentences
        .flatMap(_.split(" "))
        .map(_.split("\\|").toVector)
        .map { case Seq(w, CleanTok(l), CleanPos(p)) => (l, p) -> 1 }
    }
    .filter { case ((l, p), c) => ValidLemma(l) && !InvalidPos(p) }
    .map { case ((l, p), c) => ("%s\t%s".format(l, p), c) }
    .group
    .reduce(_ + _)
    .filter { case (lp, c) => c >= minCount }
    .map { case (lp, c) => "%s\t%s".format(lp, c) }
    .write(TypedTsv[String](outputIdfFile))

}
