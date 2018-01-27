package com.github.dyf102.sp500analysis


import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.sql.functions.{lag, abs, percent_rank, max}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window

class Main {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("Spark CSV Reader").getOrCreate
    import spark.implicits._
    // read from csv file
    val df = spark.read.format("csv").option("header", "true").option("mode", "DROPMALFORMED").load("./SP500.csv").selectExpr("COALESCE(CAST(SP500 AS DOUBLE), 0.0) as SP500", "DATE")
    // explain the format
    df.explain()
    // window function
    val w = Window.orderBy($"DATE")
    // lead function to form the data set
    val lagDf = df.withColumn("yesterday_SP500", lag("SP500", 1, 0).over(w)).sort($"DATE").filter($"yesterday_SP500" !== 0).filter($"yesterday_SP500".isNotNull)
    // do the calculation
    val result = lagDf.selectExpr("abs(SP500 - yesterday_SP500) / yesterday_SP500 AS change_percentage").withColumn("r", percent_rank().over(Window.orderBy($"change_percentage")))
    result.filter($"r" <= 0.9).agg(max($"change_percentage")).show()
  }
  def convertToDate(dateStr:String):Date = {
    val format = new SimpleDateFormat("yyyy-mm-dd")
    format.parse(dateStr)
  }
}
