FROM openjdk:8
VOLUME /tmp
ADD target/GeoQuizz-1.0-SNAPSHOT.jar GeoQuizz.jar
RUN bash -c 'touch /restservice.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/GeoQuizz.jar"]