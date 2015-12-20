FROM          maven:3
MAINTAINER    krystism <krystism@gmail.com>
RUN mkdir /build
workdir /build
ENV TARGET videosynopsis-1.0.jar
ADD pom.xml .
ADD src src
RUN mvn package && mvn test
RUN cp target/$TARGET / && rm -rf /build && rm -rf ~/.m2/*
CMD ["java", "-jar", "/videosynopsis-1.0.jar", "$@"]
