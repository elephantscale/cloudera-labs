from __future__ import print_function

import sys

from pyspark.context import SparkContext
from pyspark.mllib.tree import RandomForest
from pyspark.mllib.util import MLUtils


# Get SparkContext
sc = SparkContext(appName="PythonRandomForestClassification")
#sc.setLogLevel("ERROR") 

# Training and test data 
trainingDataFile = "file:///home/cloudera/LabFiles/Spark/MLlib/training_two_classes.svm"
testDataFile  = "file:///home/cloudera/LabFiles/Spark/MLlib/test_two_classes.svm"

trainingData = MLUtils.loadLibSVMFile(sc, trainingDataFile)
testData = MLUtils.loadLibSVMFile(sc, testDataFile)

# RandomForest model input parameters
numClasses=2
categoricalFeaturesInfo={}
numTrees=5
featureSubsetStrategy="auto"

# Train the model
model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy)

# Classify the test instances and make predictions on each of them 
predictions = model.predict(testData.map(lambda x: x.features))
labelsAndPredictions = testData.map(lambda lp: lp.label).zip(predictions)

print('================= Class Label from Test Data vs Their Predictions:')
labelsAndPredictions.foreach(lambda l: print (l))


# Print the training model
print('================= Trained model:')
print(model.toDebugString())

sc.stop()





