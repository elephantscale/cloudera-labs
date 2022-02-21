
jars='.'

for f in /home/cloudera/Works/MapReduce/jars/*; do
  if [ -f $f ]; then
    jars="$jars:$f"
  fi
done
echo -e "Compilation jars:\n$jars\n"

javac -nowarn -classpath $jars -d compiled WordCountLab.java 

