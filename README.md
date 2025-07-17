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

# jwt用 公開鍵、秘密鍵生成
cd src/main/resources
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -outform PEM -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_key.pem -nocrypt
rm -f private.pem

# application.propertiesに公開鍵、秘密鍵のパスを定義
jwt.private-key=classpath:private_key.pem
jwt.public-key=classpath:public.pem
spring.security.oauth2.resourceserver.jwt.public-key-location=${jwt.public-key}