# README
Spring boot勉強用

# 環境
Java 21<br/>
Spring boot 3.3.4<br/>
mysql 8.1<br/>

# Dockerセットアップ、および起動
docker compose up -d

# Docker spring-boot環境への接続
docker compose exec app bash

# 認証関連
<h3>jwt用 公開鍵、秘密鍵生成</h3>
cd src/main/resources<br/>
openssl genrsa -out private.pem 2048<br/>
openssl rsa -in private.pem -outform PEM -pubout -out public.pem<br/>
openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_key.pem -nocrypt<br/>
rm -f private.pem

<h3>application.propertiesに公開鍵、秘密鍵のパスを定義</h3>
jwt.private-key=classpath:private_key.pem<br/>
jwt.public-key=classpath:public.pem<br/>
spring.security.oauth2.resourceserver.jwt.public-key-location=${jwt.public-key}<br/>

<h3>参考サイト</h3>
Java 21/Spring Boot 3で実装する最新JWT認証ガイド https://zenn.dev/okamyuji/articles/0bfcc5a9b17cb5<br/>
Spring BootでJWT認証・認可の設定メモ https://ik.am/entries/818

# テストDBの作成
<ul>
<li>DBコンテナに接続</li>
docker compose exec db bash
<li>mysqlに接続</li>
mysql -u root -p<br>
パスワードは、secret（compose.ymlで定義しているもの）
<li>test databaseを作成</li>
create database test;<br>
</ul>

# テスト実行
mvn test
