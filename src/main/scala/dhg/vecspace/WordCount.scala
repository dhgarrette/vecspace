package dhg.vecspace

import com.utcompling.tacc.scalding.ScaldingJob
import com.twitter.scalding._

object WordCount extends ScaldingJob {
  def jobClass = classOf[WordCount]
}

class WordCount(args: Args) extends Job(args) {
  val (inputFile, outputFile) =
    args.positional match {
      case Seq(inputFile, outputFile) => (inputFile, outputFile)
      case _ => sys.error("WordCount requires two positional arguments: inputFile outputFile.  Found: " + args.m)
    }

  TypedPipe.from(TextLine(inputFile))
    .flatMap(_.toLowerCase.split("\\W+"))
    .map(word => (word, 1))
    .group  // group by key, same as .groupBy{ case (word, count) => word }.mapValues{ case (word, counts) => counts }
    .reduce(_ + _)  // same as .reduce((a: Int, b: Int) => a + b)
    .write(TypedTsv[(String, Int)](outputFile))
}
