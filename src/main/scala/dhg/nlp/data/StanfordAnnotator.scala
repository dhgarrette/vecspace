package dhg.nlp.data

import scala.collection.JavaConverters._
import dhg.nlp.data.AnnotatedData._
import dhg.util.CollectionUtil._

class StanfordAnnotator(
  posModelLocation: String = "stanford-corenlp-models/pos-tagger/english-left3words-distsim.tagger",
  nerModelLocation: String = "stanford-corenlp-models/ner/english.all.3class.distsim.crf.ser.gz") {

  val j = new JStanfordAnnotator(posModelLocation, nerModelLocation)

  def apply(text: String): AnnotatedDoc = {
    j.annotate(text).asScala.map(sentence =>
      sentence.asScala.map { tok =>
        AnnotatedToken(tok.word, tok.lemma, tok.pos, tok.ne)
      }.toVector).toVector
  }

  def apply(texts: Vector[String]): AnnotatedDoc = {
    texts.flatMap(apply)
  }

}

object AnnotatedData {

  case class AnnotatedToken(
    word: String,
    lemma: String,
    pos: String,
    ne: String)

  type AnnotatedSentence = Vector[AnnotatedToken]
  type AnnotatedDoc = Vector[AnnotatedSentence]
  type AnnotatedCluster = Vector[AnnotatedDoc]

  def detokenize(sentence: AnnotatedSentence) = {
    sentence.map(_.word)
      .mkString(" ")
      .replace("-LRB-", "(")
      .replace("-RRB-", ")")
      .replaceAll("([`(]) ", "$1")
      .replaceAll(" ([^`(A-Za-z0-9])", "$1")
  }

}
