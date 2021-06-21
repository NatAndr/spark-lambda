package com.example.myproject.provider

import org.apache.commons.csv.{CSVFormat, QuoteMode}

import java.io.FileReader
import scala.collection.JavaConverters.collectionAsScalaIterableConverter

object Reader {
  def getRecords(fileName: String): Iterable[YouTubeRecord] = {
    val reader = new FileReader(fileName)

    CSVFormat
      .newFormat(',')
      .withFirstRecordAsHeader
      .withQuote('"')
      .withQuoteMode(QuoteMode.MINIMAL)
      .withAutoFlush(true)
      .parse(reader)
      .getRecords.asScala
      .map(YouTubeRecord(_))
  }

}
