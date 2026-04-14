# 使用OpenJDK基础镜像
FROM eclipse-temurin:17-jdk

# 设置工作dir目录
WORKDIR /app

# 将项目的jar包复制到容器中
COPY target/back-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用运行的端口
EXPOSE 8080

# 定义容器启动时运行的命令
ENTRYPOINT ["java", "-jar", "app.jar"]
