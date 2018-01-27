## S&P 500 Analysis
 the change of S&P 500 is defined as the percentage of index change compared to the previous closing index
 
 - Data source: https://fred.stlouisfed.org/series/SP500
 - scala version: 2.11.12
 - spark version: 2.2.1
 - sbt version: 0.14.5

### How to run it
```bash
sbt assembly
spark-submit --class \
"com.github.dyf102.sp500analysis.Main" \
--master local --deploy-mode client --driver-memory 512M \
--executor-memory 512M  <path-to-fat-jar>
```

### Explain
we want to find the interval value x that 90% of the change percentage of SP500 index fall within.
The change percentage is defined as ((today_SP500) - (last_SP500)) / last_SP500.  


