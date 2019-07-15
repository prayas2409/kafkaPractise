#!/usr/bin/env python
# coding: utf-8
# In[2]:
import sys

a = sys.stdin.read()
print(a.split())

try:
	sys.stdout.write("Entered py file")
except Exception as e:
	print("Cannot return the path beacause ",e)

#         feature = cv.transform(["The movie was pleasant"])
#         self.check_result(feature,mnb)
