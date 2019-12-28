FROM openjdk:11
VOLUME /tmp

#ADD keystore.jks keystore.jks
#ADD truststore.jks truststore.jks
ADD target/bitcoin-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
