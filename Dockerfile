# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

ENV TZ=Asia/Seoul

# Set the working directory
WORKDIR /app

RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -fs /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the WAR file to the container
COPY HisSolve.war /app/app.war

# Expose port 8080
EXPOSE 9090

# Run the Spring Boot application
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.war"]
