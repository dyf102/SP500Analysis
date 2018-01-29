## S&P 500 Analysis
 the change of S&P 500 is defined as the percentage of index change compared to the previous closing index
 
 ### Prerequisite
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

### Step
- Load the csv file to a DataFrame with numeric data cleanup
- Compute the absolute value of changed percent than last close day by using *lag* function after sorted by date ascantly.
- Use either build-in percent_rank function or order by the percentage and take *total\*range(0.9 for example) rows* 
- Get the maximum of percent changed after out-of-range rows are chopped off 

### Result
- About 0.0143072

### Conclusion 
the 90% of absolute values of change percent falls into [-0.0143072, 0.0143072]
