#!/usr/bin/env python
import sys

current_key = None  # null
current_key_count = 0

# Get lines from standard input
for line in sys.stdin:
	key,count= line.strip().split('\t')
	count = int(count)
	
	if current_key == key:
		current_key_count += count
	else:
		if current_key is not None:
			print "%s has %s occurrence(s)" % (current_key, current_key_count)
		current_key = key # first assignment
		current_key_count = count # usually, start with 1

# The last key summary ...
print "%s has %s occurrence(s)" % (current_key, current_key_count)


