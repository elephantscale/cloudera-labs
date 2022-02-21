#--------------------------------------------
# Submit a jar file for execution on Spark
#--------------------------------------------
. ./setupJava8Env.sh

jarToSubmit=JavaOnSpark.jar

echo -e "Submitting $jarToSubmit for execution\n"

spark-submit  --class was.labs.spark.$1 --master local[2] $jarToSubmit

