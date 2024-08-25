# Stage 1: Build the Maven project
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
# RUN mvn dependency

COPY src/ /app/src/
RUN mvn clean install -DskipTests

FROM ubuntu:24.04
EXPOSE 8080

# Update package lists
RUN apt-get update -qq

# Install required packages
RUN apt-get install -y wget ca-certificates

# Download and install Amazon Corretto JDK (replace with desired version)
RUN wget https://corretto.aws/downloads/latest/amazon-corretto-17-x64-linux-jdk.tar.gz \
  && tar -xzf amazon-corretto-17-x64-linux-jdk.tar.gz -C /opt \
  && ln -s /opt/amazon-corretto-17.0.12.7.1-linux-x64/bin/* /usr/local/bin

# Set environment variable for JAVA_HOME
RUN export JAVA_HOME=/opt/amazon-corretto-17.0.12.7.1-linux-x64
RUN export PATH=$JAVA_HOME/bin:$PATH

# Set the default command to print the Java version
#CMD ["java", "-version"]

WORKDIR /app
COPY --from=build /app/target/*.jar my-project.jar
#
CMD ["java","-jar","my-project.jar"]