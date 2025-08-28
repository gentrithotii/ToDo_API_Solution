# Run stage
FROM eclipse-temurin:21
COPY /target/ToDo_API-0.0.1-SNAPSHOT.jar ToDo_APP-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "ToDo_APP-0.0.1-SNAPSHOT.jar"]


# docker network create todo-net
# docker run --name mysql-db --network todo-net -e MYSQL_ROOT_PASSWORD=root -p 3307:3306 -v  mysql_data:/var/lib/mysql -d mysql:8.0

# docker build -t todo-api .
# docker run --name todo-api --network todo-net  -p 9090:9090 -e SPRING_PROFILES_ACTIVE=dev-doc -e APP_USER_EMAIL=your-email@gmail.com -e APP_USER_PASSWORD=your-api-password todo-api

# docker build -t todo-api .
# docker-compose up -d