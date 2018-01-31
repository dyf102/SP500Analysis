package com.github.dyf102.sp500analysis


import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{lag, max, percent_rank}
import org.apache.spark.sql.{DataFrame, SparkSession}

object Main {
  val AppName = "Spark_SP500_Range_Calculation"
  val logger = Logger.getLogger(AppName)
  logger.setLevel(Level.DEBUG)

  def main(args: Array[String]): Unit = {
    val range: Double = 0.9
    val filePath = "./SP500.csv" // or in HDFS "hdfs:///warehouse/SP500.csv"

    // read from csv file
    val df = readCSVFile(filePath)

    // lead function to form the data set
    val lagDf = getChangePercent(df).cache() // reused result
    // do the calculation
    println(getResultByPercentRank(lagDf, range)) //method 1
    println(getResultBySort(lagDf, range)) //method 2
  }

  def readCSVFile(filePath:String):DataFrame = {
    val spark = SparkSession.builder().getOrCreate()
    spark
      .read
      .format("csv")
      .option("header", "true")
      .option("mode", "DROPMALFORMED")
      .load(filePath)
      // convert the '.' to 0
      .selectExpr("COALESCE(CAST(SP500 AS DOUBLE), 0.0) as SP500", "DATE")
  }

  def getChangePercent(df: DataFrame):DataFrame = {
    val spark: SparkSession = getSparkSession(AppName)
    import spark.implicits._
    // window function
    val w = Window.orderBy($"DATE")
    df
      .withColumn("yesterday_SP500", lag("SP500", 1, 0).over(w))
      .sort($"DATE").filter($"yesterday_SP500" !== 0)
      .filter($"yesterday_SP500".isNotNull)
      // get the changed percentage
      .selectExpr("abs(SP500 - yesterday_SP500) / yesterday_SP500 AS change_percentage")
  }
  def getResultByPercentRank(df: DataFrame, range:Double): Double = {
    val spark: SparkSession = getSparkSession(AppName)
    import spark.implicits._
    df.withColumn("r", percent_rank().over(Window.orderBy($"change_percentage")))
      .filter($"r" <= range)
      .agg(max($"change_percentage")).take(1).last.getAs(0)
  }

  def getResultBySort(df: DataFrame, range:Double): Double = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._
    val total: Int = (df.count() * range).ceil.toInt
    df
      .orderBy($"change_percentage")
      .take(total)
      .last.getAs(0)
  }

  def getSparkSession(appName: String): SparkSession = {
    val ss = SparkSession.builder().master("local[*]").appName(appName).getOrCreate
    ss.sparkContext.setLogLevel("ERROR")  //stop info log from spark console
    ss
  }
}
