package com.github.dyf102.sp500analysis

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import scala.math.BigDecimal

class AnalysisTest extends FunSuite with BeforeAndAfterEach {
  var sparkSession : SparkSession = _
  override def beforeEach() {
    if (System.getenv("hadoop.home.dir") == null && System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
      // just for the case that Windows does not have hadoop environment
        System.setProperty("hadoop.home.dir", "C:\\Users\\N7117\\Downloads\\hadoop")
    }
    sparkSession = SparkSession.builder().appName("SP500 Analysis testings")
      .master("local")
      .config("", "")
      .getOrCreate()
    sparkSession.sparkContext.setLogLevel("ERROR")
  }

  //
  val filePath = "src/test/support/SP500.csv"
  test("Load from csv file") {

    val df:DataFrame = Main.readCSVFile(filePath)
    assert(df.count() == 4L)
  }
  val expectedResult = Array[Double](0.0051, 0.0039, 0.0026)
  test("Compute the absolute of percent of change") {
    val df:DataFrame = Main.readCSVFile(filePath)
    val resultDF = Main.getChangePercent(df)
    resultDF.show()
    assert(resultDF.count() == 3L)
    val roundedResult:Array[Double] = resultDF.take(3).map(t => round(t.getDouble(0), 4))
    assert(roundedResult.deep == expectedResult.deep)
  }

  test("Method 1: Compute the range by using percent_rank") {
    val df:DataFrame = Main.readCSVFile(filePath)
    val changedPercentDF = Main.getChangePercent(df)
    val result = Main.getResultByPercentRank(changedPercentDF, 0.5)
    print(result)
    assert(round(result, 4) == 0.0039)
  }

  test("Method 2: Compute the range by simply sorting") {
    val df:DataFrame = Main.readCSVFile(filePath)
    val changedPercentDF = Main.getChangePercent(df)
    val result = Main.getResultBySort(changedPercentDF, 0.5)
    print(result)
    assert(round(result, 4) == 0.0039)
  }

  def round(value:Double, pos: Int): Double = {
    BigDecimal(value).setScale(pos, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  override def afterEach() {
    sparkSession.stop()
  }
}
