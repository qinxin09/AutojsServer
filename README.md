# AutojsServer
AutojsServer 是一个基于Android平台的本地服务器应用，主要用于为Auto.js Pro提供网络代理和服务器转发服务。

![image-20260301085838016](/assets/image-20260301085838016.png)

## 环境配置
- Android Studio Koala | 2024.1.1 Patch 1
- JAVA_VERSION="17.0.11"
- gradle-8.7-bin.zip


## 📱 项目概述

这是一个Android VPN服务应用，通过建立本地HTTP/HTTPS服务器来实现网络请求的拦截和转发。主要功能包括：
- 提供本地HTTP/HTTPS服务器
- 支持VPN模式运行
- 实现网络数据包的解析和处理
- 为Auto.js Pro提供安全验证服务

## 🛠 技术栈

### 开发环境
- **Android SDK**: 34
- **最小SDK版本**: 24 (Android 7.0)
- **目标SDK版本**: 34 (Android 14)
- **开发语言**: Kotlin + Java
- **构建工具**: Gradle 8.5.1
- **Kotlin版本**: 1.9.0

### 核心依赖
- `org.nanohttpd:nanohttpd:2.3.1` - 轻量级HTTP服务器
- `com.squareup.okhttp3:okhttp:4.9.0` - HTTP客户端
- `androidx.core:core-ktx:1.10.1` - Android核心扩展库

## 🏗 项目架构

### 主要组件

#### 1. 核心服务类
- **`MainActivity`** - 主界面Activity，负责UI交互和服务器控制
- **`LocalVPNService`** - VPN核心服务，处理网络数据包拦截和转发
- **`SecureServerService`** - 安全服务器服务，管理HTTP服务器生命周期
- **`HttpServer`** - 基于NanoHTTPD的HTTP服务器实现
- **`HttpsServer`** - HTTPS服务器实现

#### 2. 网络处理模块
- **`bio/`** - 阻塞式I/O网络处理器
    - `BioTcpHandler` - TCP连接处理器
    - `BioUdpHandler` - UDP数据包处理器
    - `NioSingleThreadTcpHandler` - 单线程NIO TCP处理器
- **`protocol/tcpip/`** - TCP/IP协议解析器
- **`util/`** - 工具类集合

#### 3. 配置管理
- **`config/Config.java`** - 全局配置管理类
- 端口配置、IP地址配置等

## 🔧 功能特性

### 主要功能
1. **VPN服务**: 通过Android VPN API建立虚拟网络接口
2. **本地服务器**: 在设备上运行HTTP/HTTPS服务器
3. **数据包拦截**: 拦截并分析网络流量
4. **协议转换**: 支持HTTP/HTTPS协议转换
5. **Auto.js Pro集成**: 专门为Auto.js Pro提供验证服务

### 网络处理能力
- TCP连接管理
- UDP数据包处理
- 数据包校验和计算
- IP地址转换
- 端口映射

## 🚀 使用方法

### 启动步骤
1. 安装应用到Android设备
2. 打开应用，授予必要的权限
3. 点击主界面按钮启动服务器
4. 应用会自动启动VPN服务和本地服务器

### 操作说明
- **启动服务**: 点击主界面按钮开始运行
- **停止服务**: 再次点击按钮停止服务
- **启动Auto.js Pro**: 可直接从应用内启动Auto.js Pro
- **获取支持**: 通过QQ群获取技术支持

## 📁 项目结构

## 🔒 安全特性

- 数据传输加密支持
- 网络流量监控
- 访问控制机制
- 日志记录功能

## 🐛 已知问题

1. 需要Android 7.0及以上版本
2. 某些设备可能存在兼容性问题
3. 需要手动授予权限

## 📞 技术支持

如需获取技术支持或反馈问题，请加入我们的QQ群：
- 群号: 975044417

## 📄 许可证

本项目仅供学习和研究使用，请遵守相关法律法规。

## 📝 版本信息

- **当前版本**: 1.0
- **目标平台**: Android 7.0+

