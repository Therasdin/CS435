#!/bin/bash

# Script to run PageRank Task 2 (With Taxation)
# Usage: ./run_task2.sh <links_file> <titles_file> <output_dir> <iterations> [beta]

if [ "$#" -lt 4 ]; then
    echo "Usage: $0 <links_file> <titles_file> <output_dir> <iterations> [beta]"
    echo "Example: $0 /data/links-simple-sorted.txt /data/titles-sorted.txt task2_output 25 0.85"
    exit 1
fi

LINKS_FILE=$1
TITLES_FILE=$2
OUTPUT_DIR=$3
ITERATIONS=$4
BETA=${5:-0.85}

JAR_FILE="target/WikiPageRank-1.0-SNAPSHOT.jar"

echo "=================================================="
echo "Running PageRank Task 2: With Taxation"
echo "=================================================="
echo "Links file: $LINKS_FILE"
echo "Titles file: $TITLES_FILE"
echo "Output directory: $OUTPUT_DIR"
echo "Iterations: $ITERATIONS"
echo "Beta (damping factor): $BETA"
echo "=================================================="

# Remove output directory if it exists
if [ -d "$OUTPUT_DIR" ]; then
    echo "Removing existing output directory..."
    rm -rf "$OUTPUT_DIR"
fi

# Run Spark job
spark-submit \
    --class pagerank.PageRankTaxation \
    --master local[*] \
    --driver-memory 4g \
    --executor-memory 4g \
    "$JAR_FILE" \
    "$LINKS_FILE" \
    "$TITLES_FILE" \
    "$OUTPUT_DIR" \
    "$ITERATIONS" \
    "$BETA"

echo ""
echo "Task 2 completed!"
echo "Results saved to: $OUTPUT_DIR"