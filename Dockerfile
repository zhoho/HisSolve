# Base image for Python
FROM python:3.8-slim AS python

# Base image for Java
FROM openjdk:11-slim AS java

# Base image for C
FROM gcc:latest AS c

# Set the working directory
WORKDIR /app

# Copy the current directory contents into the container
COPY . /app

# Command to run the Python file (can be overridden)
CMD ["python", "TempCode.py"]

# Command to compile and run Java file (can be overridden)
CMD ["sh", "-c", "javac TempCode.java && java TempCode"]

# Command to compile and run C file (can be overridden)
CMD ["sh", "-c", "gcc -o TempCode TempCode.c && ./TempCode"]
