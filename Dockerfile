# Use an official OpenJDK runtime as a parent image
FROM tomcat:9-jdk17-openjdk

# Set the working directory
WORKDIR /app

RUN apt-get update && apt-get install -y docker.io

# Copy the WAR file to the container
COPY HisSolve.war /app/app.war

# Expose port 8080
EXPOSE 9090

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.war"]
