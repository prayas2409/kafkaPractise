#!/usr/bin/env python
# coding: utf-8
# In[2]:
import sys
import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import cross_val_score,cross_validate,train_test_split
from sklearn.preprocessing import StandardScaler,Normalizer, RobustScaler
from sklearn.metrics import *
import pickle

a = sys.stdin.read()
if a=="":
    sys.exit()

close,high,low,volume,open = a.split(" ")

arr = np.array([close,volume,high,low]).reshape(-1,1)


try:
	sys.stdout.write("Entered py file")
except Exception as e:
	print("Cannot return the path beacause ",e)

#         feature = cv.transform(["The movie was pleasant"])
#         self.check_result(feature,mnb)
