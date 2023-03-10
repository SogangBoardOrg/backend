FROM amazoncorretto:17.0.1 AS builder

COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle gradle
COPY src src
COPY securityTest securityTest
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM amazoncorretto:17.0.1

RUN mkdir /opt/app
COPY --from=builder build/libs/*.jar /opt/app/spring-boot-application.jar
EXPOSE 8080
EXPOSE 3306
ENV	PROFILE local
#ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}" ,"/opt/app/spring-boot-application.jar"]