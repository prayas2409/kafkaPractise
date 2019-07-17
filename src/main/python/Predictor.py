#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.metrics import *
import sys
import pickle
import os
import numpy as np


# In[2]:


a = sys.stdin.read()


# In[3]:


# a = '135.7600 135.7800 135.4100 135.4100 291093'


# In[4]:


test_list = list(np.float_(a.split()))


# In[5]:


close,high,low,open1,volume = test_list


# In[6]:


data = np.array([close,volume,high,low]).reshape(1,-1)


# In[7]:


print(data.shape)


# In[8]:


model_path = "Model/model.pkl"


# In[9]:


with open(model_path,'rb') as f:
    model = pickle.load(f)


# In[10]:


pred = model.predict(data)


# In[11]:


print("predicted: {}, Actual:{}".format(pred[0],open1))


# In[12]:


err_mae = mean_absolute_error(np.array(open1).reshape(1,-1),pred)


# In[13]:


print("Accuracy = {}".format(100-err_mae))


# In[ ]:


try:
    sys.stdout.write(pred)
except Exception as e:
        print("Cannot return the path beacause ",e)

