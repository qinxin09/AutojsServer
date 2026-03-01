# **AutojsServer - Android 本地流量拦截与分析**

📢 **项目声明**：
本项目仅用于**安卓逆向工程技术研究**、**通信协议分析**及**个人学习交流**。

- **核心机制**：利用 Android `VpnService` 构建**本地虚拟网卡**，将设备流量**100% 本地回环转发**至内置的 NanoHTTPD 服务器。
- **安全承诺**：所有流量处理均在**设备本地内存中完成**，**绝不上传、绝不外传、绝不连接外部代理节点**。
- **免责条款**：严禁将本项目用于任何商业用途、非法入侵或侵犯他人隐私的行为。如因不当使用产生的法律纠纷，使用者需自行承担全部责任。

## **🚀 项目简介**

AutojsServer 是一个基于 Android 平台的高级网络分析工具。它通过实现自定义的 `VpnService`，在无需 Root 权限的情况下，建立了一条**本地流量隧道**。所有经过该隧道的网络请求都会被透明拦截，并转发至本地运行的 **NanoHTTPD** 轻量级服务器进行实时捕获、解析与重放。

本项目旨在深入探索：

1. **Android 网络底层机制**：`VpnService`、TUN 设备驱动、IP 包封装与解包。
2. **协议分析技术**：HTTP/HTTPS 流量的实时抓取、Header 分析及 Body 数据还原。
3. **中间人（MITM）原理**：在本地环境下模拟服务器响应，用于调试和分析封闭协议（如 Auto.js Pro 的通信逻辑）。

![image-20260301085838016](E:\AndroidStudio\AutojsServer\assets\image-20260301085838016-1772329496837-4.png)

## **🛠️ 核心技术栈**

- **Android Framework**: 深度定制 `VpnService`，配置 `Builder` 路由规则，实现全设备流量接管。
- **网络编程**: 原生 Socket 编程，处理 TCP/UDP 数据包的转发处理。
- **本地服务**: 集成 **NanoHTTPD** 作为本地接收端，实现高性能的 HTTP 请求监听与动态响应。
- **逆向工程**: 结合抓包数据，对目标应用（Auto.js Pro）进行协议逆向、参数加密分析及接口模拟。

## **💡 应用场景（学习用途）**

- **协议逆向**：在不依赖外部抓包工具的情况下，直接获取 App 内部的加密通信协议。
- **接口 Mock**：本地拦截请求后，由 NanoHTTPD 返回自定义的 Mock 数据，用于测试 App 的异常处理能力。
- **安全研究**：分析 App 是否存在明文传输、证书校验绕过等安全隐患。

## 环境配置
- Android Studio Koala | 2024.1.1 Patch 1
- JAVA_VERSION="17.0.11"
- gradle-8.7-bin.zip

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

