from __future__ import print_function
import numpy as np
from pyspark import SparkContext
from pyspark.mllib.clustering import KMeans
from pyspark.mllib.clustering import KMeansModel

inputFile = "file:///home/cloudera/LabFiles/Spark/MLlib/kmeans_clusters.dat"
k =  3

lines = sc.textFile(inputFile)
lines.foreach(print)

data = lines.map(lambda line: np.array([float(x) for x in line.split(' ')]))

model = KMeans.train(data, k, runs=10)

for x in model.clusterCenters: print(x)

model.predict ([8,9])

model.predict ([1,3])
