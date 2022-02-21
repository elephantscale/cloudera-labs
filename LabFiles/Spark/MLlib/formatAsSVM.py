#import
#fileIn = "test_three_classes.dat"
#fileOut = "test_three_classes.svm"

fileIn = "two_classes.dat"
fileOut = "training_two_classes.svm"


fr = open (fileIn, "r")
fw = open (fileOut, "w")

lineCnt = 0

# Lables for the SVM format used by MLlib in Spark

label = 0
# ------------------------------------ 
def get3Label(lineCnt):
   label = 0
   if (lineCnt > 10 and lineCnt < 21):
      label = 1

   if (lineCnt > 20):
      label = 2
      
   return label
# ------------------------------------   
def get2Label (lineCnt):
   return 0 if lineCnt <= 10 else 1
     

# ------------------------------------ 
for line in fr:
   lineCnt += 1;
   la = (line.split(' '))

  #  label = get3Label (lineCnt)  
   label = get2Label (lineCnt) 
   ls = str(label) + " 1:" + str(la[0]) +" 2:"+ str(la[1])
   print (ls)
   fw.write(ls)
   








   
  