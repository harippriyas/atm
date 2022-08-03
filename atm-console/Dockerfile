FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

RUN mkdir /opt/app
COPY target/atm-console-1.0.jar /opt/app/
CMD ["java", "-jar", "/opt/app/atm-console-1.0.jar"]