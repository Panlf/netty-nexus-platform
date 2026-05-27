# netty-nexus-platform



<div align="center">
    <p>Netty 学习者的一站式案例仓库</p>
    <img src="https://img.shields.io/badge/Java-8%2B-brightgreen.svg" alt="Java Version">
    <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License">
    <img src="https://img.shields.io/badge/Netty-5.0.0.Alpha2-red.svg" alt="Status">
</div>

## 项目简介

`netty-nexus-platform` 是一个基于 **Netty 5.0.0.Alpha2**（`netty-all`）的学习型项目，运行在 **Java 8** 环境下，使用 **MIT 协议**开源。

项目通过多个独立模块，循序渐进地展示 Netty 在不同场景下的应用：从基础语法入门，到文件传输、二进制数据通信、Web 页面服务、WebSocket 聊天、RPC 框架设计，再到与 **LMAX Disruptor** 整合的高性能消息传递。每个模块都是一个完整的可运行案例，适合 Netty 初学者与进阶者参考实践。

---

## 模块说明

项目共分为 7 个独立模块，按功能与学习顺序组织。每个模块对应一个独立的包或主类，可以通过直接运行相应启动类来观察效果。

### 1. 学习 Netty 的基础语法和功能

> 模块定位：**入门与热身**

该模块包含 Netty 最核心的基础示例，如 **Echo 服务**（客户端与服务端互相回显消息）、**ChannelHandler 生命周期演示**、**Pipeline 顺序编排**、**ByteBuf 的使用**等。通过本模块可以快速理解 Netty 的事件驱动模型、责任链模式以及异步编程风格。

### 2. 基于 Netty 进行二进制数据传输

> 模块定位：**自定义协议与编解码**

该模块演示如何在 Netty 中传输二进制数据（如 `byte[]`、序列化对象、自定义消息头+体）。实现了 **长度字段分帧解码器**（`LengthFieldBasedFrameDecoder`）与对应的编码器，展示了如何处理粘包/拆包问题，适用于底层二进制协议（如私有 RPC 协议、多媒体数据流）的开发。

### 3. 基于 Netty 实现文件上传功能

> 模块定位：**大文件处理与零拷贝**

该模块实现了一个完整的文件上传服务端与客户端。支持分片上传、断点续传（可选）和 **FileRegion 零拷贝**传输。展示了 Netty 中 `DefaultFileRegion` 和 `ChunkedWriteHandler` 的用法，能够高效地将本地文件发送到对端，避免数据在用户态与内核态之间的多次拷贝。

### 4. 使用 Netty 实现 Web 页面

> 模块定位：**HTTP 协议与静态资源服务**

该模块将 Netty 作为嵌入式 HTTP 服务器，能够托管 HTML、CSS、JavaScript 等静态资源。通过实现 `HttpRequestHandler` 和 `HttpResponse` 构造，返回正确的 MIME 类型。用户可以通过浏览器访问 `http://localhost:8080` 看到示例页面，理解 Netty 如何处理 HTTP 请求响应。

### 5. 基于 Netty 实现 WebSocket 的聊天功能

> 模块定位：**全双工实时通信**

该模块基于 **WebSocket 协议**，实现了一个简单的多人聊天室。服务端使用 `WebSocketServerProtocolHandler` 升级 HTTP 连接至 WebSocket，并通过 `TextWebSocketFrame` 广播消息。客户端可以用浏览器自带的 WebSocket API 连接并收发消息。该模块展示了 Netty 对长连接、推送服务的天然支持。

### 6. 基于 Netty 实现 RPC 功能

> 模块定位：**远程过程调用框架雏形**

该模块实现了一个轻量级 RPC 框架。包括：
- 服务端：发布接口实现，通过 Netty 接收请求，利用反射调用本地方法并返回结果。
- 客户端：使用动态代理生成接口代理，将方法调用信息（类名、方法名、参数）编码后通过 Netty 发送给服务端，并同步阻塞等待响应（或异步回调）。  
  通过该模块可以理解 RPC 的核心原理：协议设计、序列化、网络传输与代理机制。

### 7. Netty 整合 Disruptor 进行消息的传递

> 模块定位：**极致性能优化**

该模块将 Netty 的 I/O 线程与业务处理线程彻底分离。Netty 的 `ChannelHandler` 接收消息后，将消息发布到 **LMAX Disruptor**（无锁环形队列）中，由独立的消费者线程池进行业务处理（如数据库写入、复杂计算）。这种设计避免了 I/O 线程被耗时任务阻塞，极大提升了系统吞吐量。该模块展示了如何在高性能中间件中应用 Disruptor 与 Netty 的整合。

---

## 后续计划

本项目将持续收集和整理 **Netty 的高级应用场景**与性能调优实践，计划后续加入的内容包括：

- 基于 Netty 的 HTTP/2 示例
- Netty 与 Protobuf 的高效编解码整合
- 内存池与对象池的优化实践
- 基于 Netty 的代理服务器（正向/反向）
- 生产环境中的 Netty 监控与指标采集

