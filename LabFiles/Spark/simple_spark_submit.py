from __future__ import print_function
from time import sleep
from pyspark import SparkConf, SparkContext
import sys

from pyspark.context import SparkContext


# Get SparkContext
sc = SparkContext(appName="PythonSimpleSparkApp")
# sc.setLogLevel("ERROR") 

#print ("========================== Application ID: ")
#print (sc.applicationId)

print ("========================== Application Name:")
print (sc.appName)

print ("Application will run with these arguments: " + str(sys.argv))

sleep_sec = 2

if (len(sys.argv) > 1):
   print (sys.argv[1])
   sleep_sec = float(sys.argv[1])
   

print ("About to sleep for " + str(sleep_sec) + " seconds ...")

sleep(sleep_sec) # sleep the current's thread for # seconds

print("=========================== Back to business after sleeping for " + str(sleep_sec) + " seconds.")

print(SparkConf().getAll())

sc.stop() 

# Return control to the driver 

