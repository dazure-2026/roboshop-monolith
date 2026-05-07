.PHONY: build run run-docker stop-docker db-init load-test clean

build:
	./mvnw clean package -DskipTests

run:
	./mvnw spring-boot:run

run-docker:
	docker-compose up --build -d

stop-docker:
	docker-compose down

stop-docker-clean:
	docker-compose down -v

db-init:
	mysql -h $${MYSQL_HOST:-localhost} -u root -pRoboShop@1 < src/main/resources/db/schema.sql
	mysql -h $${MYSQL_HOST:-localhost} -u root -pRoboShop@1 roboshop < src/main/resources/db/master-data.sql

load-test:
	k6 run load-test/load-test.js

clean:
	./mvnw clean
