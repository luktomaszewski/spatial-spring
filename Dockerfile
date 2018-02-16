FROM openjdk:8-jdk-alpine
RUN apk add --update bash
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ADD build/libs/*.jar /app.jar
