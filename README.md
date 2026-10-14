# FP Lab 2: Big Data (Scala + Spark), variant 2

Dataset: [Exports and Imports of India (1997-2022)](https://www.kaggle.com/ramjasmaurya/exports-and-imports-of-india19972022), saved as `data/india_trade.csv`.

Run (JDK 17, sbt):

```
sbt run
```

All tasks are in `src/main/scala/Main.scala`. The result of task 10 is written to `output/india_trade_result/`.

`hadoop/bin` contains `winutils.exe` and `hadoop.dll`, which Spark needs on Windows to write files.
