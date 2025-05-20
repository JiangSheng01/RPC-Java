# RPC-Java

使用的技术及其相应版本：

+ Java: 23.0.2
+ zookeeper: 3.6.4
+ netty: 4.1.121
+ maven: 3.9.9

## 当前实现的功能

version 1:
+ 实现基本的rpc调用
+ 客户端使用动态代理
+ 客户端和服务器之间的使用netty进行信息传输
+ 自定义消息格式
+ 引入zookeeper作为注册中心

version 2:
+ 使用netty提供的基类实现自定义的编码器和解码器
+ 使用fastjson自定义序列化器
+ 在客户端加入了本地服务缓存并通过zookeeper的发布/订阅功能实现缓存的动态更新