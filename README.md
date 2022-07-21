# APIExplorer
An API explorer based on curiosity-driven Q-Learning

# 项目结构           
```
├─pom.xml                       // maven依赖，主要加入了NDArray的包
│      
├─src
│  ├─main
│  │  ├─java
│  │  │      APIExplorer.java   //入口类，包含了图处理以及整个流程
│  │  │      QLearning.java     //Q-Learning强化学习类
│  │  │      Utils.java         //工具类
│  │  │      
│  │  └─resources
│  │      └─graph               //保存dot文件
│  │           odg.dot
│  │              
│  └─test
│      └─java
└─README.md                     // help
```

# TODO
1.重置测试系统状态

2.过程中实时发送请求，目前是使用随机数模拟请求是否成功
