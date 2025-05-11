# Kronos

Kronos 是一个 Todo 系统，后端使用 **Ktor** + **Exposed** + **Lettuce** 构建的高性能后端服务骨架。前端预计使用 [Kuikly](https://github.com/Tencent-TDS/KuiklyUI)，目前仍在开发中...

## 功能

- 使用 Exposed ORM 简化与 MySQL 的数据交互
- 通过 Lettuce 驱动进行 Redis 管理
- 支持 Kotlin 协程，提供非阻塞异步处理
- 可扩展的模块化结构，便于后续功能集成

## 环境要求

- JDK 11+
- MySQL 8+
- Redis 5+

## 快速开始

1. 通过 GitHub Actions 下载或本地自行构建 Fat Jar
	
	1. 克隆仓库：
	  ```shell
	  git clone https://github.com/DongShaoNB/Kronos.git
	  cd Kronos
	  ```
	2. 构建 Fat Jar
	  ```shell
	  ./gradlew buildFatJar
	  ```
	3. Fat Jar 输出在 `build/libs/Kronos-*.*.*.jar`

> [!WARNING]  
> 请务必重命名 application.yaml 为其他名字，至少不能是 application.yaml

2. 下载 [application.yaml](https://github.com/DongShaoNB/Kronos/blob/main/src/main/resources/application.yaml) 文件，重命名为 `config.yaml` 放在 Kronos.jar 同一目录下，根据情况进行修改配置


3. 启动 Kronos 并指定配置文件
```shell
java -jar Kronos.jar -config="config.yaml"
```

## 贡献指南

欢迎提交 issue 和 pull request：

1. Fork 本仓库
2. 新建分支：`git checkout -b feature/新功能`
3. 提交修改：`git commit -m "Add new feature"`
4. 推送到远程：`git push origin feature/新功能`
5. 创建 Pull Request

## 协议

本项目采用 GPL v3.0 协议，详见 [LICENSE](LICENSE)
