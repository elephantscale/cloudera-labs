#-----------------------------------------------------------
# Compile and build the Spark Java application jar file
#------------------------------------------------------------

. ./setupJava8Env.sh


rm -Rf bin/*

jars='.'

for f in lib/*; do
  if [ -f $f ]; then
    jars="$jars:$f"
  fi
done

echo -e "Compilation jars:\n$jars\n"

javafile=$1.java
jarfile=JavaOnSpark.jar

echo  -e "Compiling $javafile \n"
javac -nowarn -classpath $jars -d bin $javafile 

echo  -e "Building $jarfile \n"
jar -cvf $jarfile -C bin/ .

#--------------------------------------------
# Submit a jar file for execution on Spark
#--------------------------------------------

jarToSubmit=JavaOnSpark.jar

echo -e "Submitting $jarToSubmit for execution\n"

spark-submit  --class was.labs.spark.$1 --master local[2] $jarToSubmit

