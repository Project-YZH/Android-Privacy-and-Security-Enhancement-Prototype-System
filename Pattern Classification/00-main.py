# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
import pandas as pd
import tensorflow as tf

# 类别的名称
SPECIES = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12',
           '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23',
           '24', '25', '26', '27', '28', '29', '30', '31', '32', '33', '34',
           '35', '36', '37', '38', '39', '40', '41', '42', '43', '44']

# 解析训练集
data_train=pd.read_csv('10/training.csv',header=0) #将csv文件的第0行作为列名
# 解析测试集
data_test=pd.read_csv('10/test.csv',header=0)
data_train.head()

# 提取特征和标签
train_x, train_y = data_train, data_train.pop('Species')
test_x, test_y = data_test, data_test.pop('Species')
train_x.head()

# 为所有特征创建feature columns（数据结构）
my_feature_columns = []
for key in train_x.keys():
    my_feature_columns.append(tf.feature_column.numeric_column(key=key))
    
classifier = tf.estimator.DNNClassifier(       
        feature_columns=my_feature_columns,   # 这个模型接受哪些输入的特征     
        hidden_units=[100,100,100],         # 包含3个隐层，每个隐层包含100个神经元  
        n_classes=45)                # 最终结果要分成45类（0-43类是恶意软件家族，44类是良性应用）  

# 用于训练的输入函数
def train_input_fn(train_x,train_y,batch_size):    
    # 将输入特征和标签转换为一个tf.data.Dataset对象
    dataset=tf.data.Dataset.from_tensor_slices((dict(train_x), train_y))    
    
    # tf.data.Dataset.shuffle对样本进行随机化处理，随机排列的样本训练效果最好
    # shuffle(1000)大于样本数，确保数据得到充分的随机化处理
    # tf.data.Dataset.repeat会在结束时重启Dataset
    # tf.data.Dataset.batch通过组合多个样本来创建一个批次，train一次处理一批
    dataset = dataset.shuffle(1000).repeat().batch(batch_size)    
    
    return dataset

# 训练
classifier.train(
    input_fn=lambda:train_input_fn(train_x,train_y,100),
    # 指示train在完成指定的迭代次数后停止训练
    steps=1000)

# 用于评估或预测的输入函数
def eval_input_fn(features, labels, batch_size):    
    features=dict(features)    
    if labels is None:            
        inputs = features    # 预测：特征（无标签）
    else:        
        inputs = (features, labels)   # 评估：特征+标签  
    # 将输入特征转换为一个tf.data.Dataset对象
    dataset = tf.data.Dataset.from_tensor_slices(inputs)     
    
    # 分批处理样本
    assert batch_size is not None, "batch_size must not be None"    
    dataset = dataset.batch(batch_size)    
    
    return dataset

# 用测试集来评估模型
eval_result = classifier.evaluate(
              input_fn=lambda:eval_input_fn(test_x,test_y,100))
print('\nTest set accuracy: {accuracy:0.3f}\n'.format(**eval_result))

# 预测
predictions = classifier.predict(input_fn=lambda:eval_input_fn(test_x,None,100))

template = ('\nPrediction is "{}" ({:.1f}%)')

# 遍历返回的predictions，以报告每个预测  
for predict in predictions:
    # predictions中的每个predict都有两个键
    # class_ids键：一个单元素数组，标识可能性最大的类别
    class_id = predict['class_ids'][0]
    # probabilities键：由三个浮点值组成的列表，分别表示输入样本是特定类别的概率
    probability = predict['probabilities'][class_id] # 最有可能的类别概率
    print(template.format(SPECIES[class_id], 100 * probability))