#---------------------------
# Using Hadoop streaming API
# --------------------------

export SLIB=/usr/lib/hadoop-mapreduce/hadoop-streaming-2.6.0-cdh5.4.0.jar

hadoop jar $SLIB  \
    -input    IN  \
    -output   OUT \
    -mapper   "/home/cloudera/Works/Streaming/mapper.py" \
    -reducer  "/home/cloudera/Works/Streaming/reducer.py"
