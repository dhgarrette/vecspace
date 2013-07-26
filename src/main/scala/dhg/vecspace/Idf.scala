package dhg.vecspace

import org.apache.commons.logging.LogFactory
import dhg.util.CollectionUtil._
import dhg.util.FileUtil._
import com.twitter.scalding._
import com.utcompling.tacc.scalding.ScaldingJob

/**
 * HOW TO RUN:
 * sbt assembly
 * hadoop fs -put /scratch/01899/dhg1/nytgiga.lem nytgiga.lem
 * hadoop jar target/scalabha-assembly.jar dhg.vecspace.BowGenerate nytgiga.lem
 * hadoop fs -getmerge nytgiga.lem.vc.f2000.m50.wInf.txt /scratch/01899/dhg1/nytgiga.lem.vc.f2000.m50.wInf.txt
 */
object Idf extends ScaldingJob {
  def jobClass = classOf[Idf2]
}

class Idf2(args: Args) extends Job(args) {

  val LOG = LogFactory.getLog(Idf.getClass)

  val HasLetter = ".*[A-Za-z].*".r
  val Punctuation = (w: String) => Set(".", ",", "``", "''", "'", "`", "--", ":", ";", "-RRB-", "-LRB-", "?", "!", "-RCB-", "-LCB-", "...", "-", "_")(w.toUpperCase)
  val ValidToken = (w: String) => !Stopwords(w.toLowerCase) && HasLetter.pattern.matcher(w).matches && !Punctuation(w)

  val Log2 = math.log(2)

  def main(args: Array[String]) {
    //Logger.getRootLogger.setLevel(Level.INFO)
    //Logger.getLogger("utcompling").setLevel(Level.DEBUG)

    //    val DEFAULT_NUM_FEATURES = "2000"
    //    val DEFAULT_MIN_WORD_COUNT = "50"
    //    val DEFAULT_WINDOW_SIZE = "Inf"
    //
    //    var additionalArgs: List[String] = Nil
    //    if (args.size + additionalArgs.size < 4)
    //      additionalArgs ::= DEFAULT_WINDOW_SIZE
    //    if (args.size + additionalArgs.size < 4)
    //      additionalArgs ::= DEFAULT_MIN_WORD_COUNT
    //    if (args.size + additionalArgs.size < 4)
    //      additionalArgs ::= DEFAULT_NUM_FEATURES
    //    if (args.size + additionalArgs.size < 4)
    //      throw new RuntimeException("Expected arguments: inputFile, numFeatures, minWordCount, windowSize")
    //
    //    val List(inputFile, numFeaturesString, minWordCountString, windowSizeString) = args.toList ++ additionalArgs
    //    val outputFile = "%s.vc.f%s.m%s.w%s.txt".format(inputFile, numFeaturesString, minWordCountString, windowSizeString)
    //    val numFeatures = numFeaturesString.toInt
    //    val minWordCount = minWordCountString.toInt
    //    val windowSize = windowSizeString.toLowerCase match { case "inf" => 10000; case s => s.toInt }

    val inputDir = "data/giga-full-apl"
    val InputFilenameRe = """.*\.apl""".r

    File(inputDir).ls(InputFilenameRe).map { f =>
      TypedPipe.from(TextLine(f.getPath))
      	
      	.flatMap(line => line.split("\\W+"))
    }

  }

}
