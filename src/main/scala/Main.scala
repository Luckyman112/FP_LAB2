import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object Main {

  // Task 9: lowest value of the column with index n (0-based, as in df.columns)
  def lowestValue(df: DataFrame, n: Int): Any = {
    require(n >= 0 && n < df.columns.length, s"column number must be in 0..${df.columns.length - 1}")
    val name = df.columns(n)
    df.agg(min(col(name))).first().get(0)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("FP Lab 2")
      .master("local[*]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    // Task 2
    val raw = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("data/india_trade.csv")

    // big numbers are written as "1,527.42", so these columns come in as strings
    val numeric = Seq("Export", "Import", "Total Trade", "Trade Balance")
    val df = numeric.foldLeft(raw) { (acc, c) =>
      acc.withColumn(c, regexp_replace(col(c), ",", "").cast("double"))
    }
    df.printSchema()

    // Task 3
    println("Task 3. Header:")
    println(df.columns.mkString(" | "))

    // Task 4
    println("Task 4. First 5 rows:")
    df.show(5)

    // Task 5
    println("Task 5. Sorted descending by Country:")
    val byCountry = df.orderBy(col("Country").desc)
    byCountry.show()

    // Task 6
    println("Task 6. Without Financial Year(end):")
    val noYearEnd = byCountry.drop("Financial Year(end)")
    noYearEnd.show()

    // Task 7
    println("Task 7. Sorted by highest Export:")
    val byExport = noYearEnd.orderBy(col("Export").desc)
    byExport.show()

    // Task 8
    println("Task 8. Mean of Total Trade and Trade Balance:")
    val withMean = byExport.withColumn(
      "Mean Trade",
      round((col("Total Trade") + col("Trade Balance")) / 2, 2)
    )
    withMean.show()

    // Task 9
    println("Task 9. Lowest value by column number:")
    withMean.columns.indices.foreach { i =>
      println(s"  $i ${withMean.columns(i)}: ${lowestValue(withMean, i)}")
    }

    // Task 10
    withMean.coalesce(1)
      .write
      .mode("overwrite")
      .option("header", "true")
      .csv("output/india_trade_result")
    println("Task 10. Saved to output/india_trade_result")

    spark.stop()
  }
}
