FROM openjdk:21-jdk-slim

# Maven をインストール
RUN apt-get update && apt-get -y install maven default-mysql-client

# 作業ディレクトリを設定
WORKDIR /app

# 依存関係を事前にダウンロード（ビルド時間短縮のため）
COPY ./pom.xml .
RUN mvn dependency:go-offline -B

# プロジェクトのソースコードをコピー
RUN mkdir src
COPY ./src src

# JAR ファイルを作成（テストはスキップ）
RUN mvn clean package -DskipTests

# ホットリロードを有効にする環境変数を設定
#ENV JAVA_OPTS="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true"
#ENV JAVA_TOOL_OPTTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5050"

EXPOSE 8080
EXPOSE 5050

#ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5050", "target/spring-boot-0.0.1-SNAPSHOT.jar"]