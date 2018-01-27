package com.github.dyf102.sp500analysis


import java.text.SimpleDateFormat
import java.util.Date

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{lag, max, percent_rank}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.expressions.Window

object Main {
  val AppName = "Spark SP500 Range Calculation"
  val logger = Logger.getLogger(AppName)
  logger.setLevel(Level.DEBUG)
  val range:Double = 0.9
  val filePath = "./SP500.csv"

  def main(args: Array[String]): Unit = {
    val spark:SparkSession = getSparkSession(AppName)
    import spark.implicits._
    // read from csv file
    val df = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("mode", "DROPMALFORMED")
      .load(filePath)
      // convert the '.' to 0
      .selectExpr("COALESCE(CAST(SP500 AS DOUBLE), 0.0) as SP500", "DATE")

    // explain the format
    // df.explain()
    // window function
    val w = Window.orderBy($"DATE")
    // lead function to form the data set
    val lagDf = df
      .withColumn("yesterday_SP500", lag("SP500", 1, 0).over(w))
      .sort($"DATE").filter($"yesterday_SP500" !== 0)
      .filter($"yesterday_SP500".isNotNull)
      // get the changed percentage
      .selectExpr("abs(SP500 - yesterday_SP500) / yesterday_SP500 AS change_percentage")
      .cache() // reused result
    // do the calculation
    println(getResultByPercentRank(lagDf))
    println(getResultBySort(lagDf))

  }
  def getResultByPercentRank(df: DataFrame):Double = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._
    df.withColumn("r", percent_rank().over(Window.orderBy($"change_percentage")))
      .filter($"r" <= range)
      .agg(max($"change_percentage")).take(1).last.getAs(0)
  }
  def getResultBySort(df: DataFrame):Double = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._
    val total:Int = (df.count() * range).ceil.toInt
    df
      .orderBy($"change_percentage")
      .take(total)
      .last.getAs(0)
  }
  def getSparkSession(appName:String):SparkSession = {
    SparkSession.builder().master("local[*]").appName(appName).getOrCreate
  }
}
