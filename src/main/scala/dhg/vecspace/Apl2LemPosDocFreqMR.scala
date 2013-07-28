package dhg.vecspace

import com.utcompling.tacc.scalding.ScaldingJob
import com.twitter.scalding._
import dhg.nlp.data._
import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import dhg.nlp.data.AnnotatedData.AnnotatedToken
import dhg.nlp.data.Giga2TokLemPos.CleanTok
import dhg.nlp.data.Giga2TokLemPos.CleanPos

object Apl2LemPosDocFreqMR extends ScaldingJob {
  def jobClass = classOf[Apl2LemPosDocFreqMRClass]

}

class Apl2LemPosDocFreqMRClass(args: Args) extends Job(args) {
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
        val sentences = paragraphs.par.flatMap((paragraph: String) =>Giga2TokLemPos.annotator.apply(paragraph))
        val annToks = sentences.flatten
        val validLps = annToks.collect { case AnnotatedToken(w, CleanTok(l), CleanPos(p), _)
                  if Idf.ValidLemma(l) && !Idf.InvalidPos(p)
                     => (l, p) }
        val validLpSet = validLps.toSet
        validLpSet.map { case (l,p) => "%s\t%s".format(l,p) -> 1 }.seq
    }
    .group
    .reduce(_ + _)
    .map { case (lp, c) => "%s\t%s".format(lp, c) }
    .write(TypedTsv[String](outputTlpFile))
}

object NonMRApl2LemPosDocFreq {
  def main(args: Array[String]) {
    val inputAplFile = "/scratch/01899/dhg1/gigaword5-apl00-10k"
    File(inputAplFile).readLines.zipWithIndex
      .map { case (x,i) => if(i%1000==0) println(i); x }
      .map(_.trim)
      .filter(_.nonEmpty)
      .flatMap { articleLine =>
        val Vector(id, typ, paragraphs @ _*) = articleLine.split("\t").toVector
        val sentences = paragraphs.flatMap((paragraph: String) =>Giga2TokLemPos.annotator.apply(paragraph))
        val annToks = sentences.flatten
        val validLps = annToks.collect { case AnnotatedToken(w, CleanTok(l), CleanPos(p), _) 
                  if Idf.ValidLemma(l) && !Idf.InvalidPos(p) 
                     && Set("year","make")(l)
                     => (l, p) }
        val validLpSet: Set[(String,String)] = validLps.toSet
        validLpSet.mapToVal(1)
    }
    .groupByKey
    .mapVals(_.reduce(_+_))
    .map { case (lp, c) => "%s\t%s".format(lp, c) }
    .foreach(println)
   
  }
}





