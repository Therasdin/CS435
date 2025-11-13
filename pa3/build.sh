#!/bin/bash

echo "Building WikiPageRank project..."
mvn clean package

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR created: target/WikiPageRank-1.0-SNAPSHOT.jar"
else
    echo "Build failed!"
    exit 1
fi