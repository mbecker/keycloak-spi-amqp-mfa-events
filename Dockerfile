# TODO
# The recommend process of using multi-stage docker builds for maven doesn't work
# See: https://github.com/carlossg/docker-maven#multi-stage-builds

# Commented commands from recommended Dockerfile:
# RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
# COPY . .
# RUN mvn -B -e -o -T 1C verify
# build all dependencies for offline use

# build
# FROM maven
# WORKDIR /usr/src/app
# COPY pom.xml .
# RUN mvn dependency:go-offline -B
# COPY . .
# RUN mvn package

# package without maven
FROM alpine:3.9
RUN mkdir -p /deployments
# COPY --from=0 /usr/src/app/target/*.jar ./deployments
COPY ./ear-module/target/com.mbecker-keycloak-spi-amqp-mfa-events-bundle-0.1-SNAPSHOT.ear ./deployments/com.mbecker-keycloak-spi-amqp-mfa-events-bundle-0.1-SNAPSHOT.ear