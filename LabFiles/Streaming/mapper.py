#!/usr/bin/env python
import sys

emitCount = 1

# Get lines from standard input
for line in sys.stdin:
    lineOfWords = line.strip().split()
    for word in lineOfWords:
        print '%s\t%s' % (word,emitCount)
        
# To test:
# cat file.dat | ./mapper.py
# echo "list of words" | ./mapper.py
