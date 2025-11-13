#!/bin/bash

# Script to run PageRank Task 3 (Wikipedia Bomb)
# Usage: ./run_task3.sh <links_file> <titles_file> <target_page> <anchor_text> <iterations>

if [ "$#" -lt 5 ]; then
    echo "Usage: $0 <links_file> <titles_file> <target_page> <anchor_text> <iterations>"
    echo "Example: $0 /data/links-simple-sorted.txt /data/titles-sorted.txt 'Rocky_Mountain_National_Park' 'surfing' 25"
    exit 1
fi

LINKS_FILE=$1
TITLES_FILE=$2
TARGET_PAGE=$3
ANCHOR_TEXT=$4
ITERATIONS=$5
BETA=${6:-0.85}

OUTPUT_LINKS="modified_links_${ANCHOR_TEXT}.txt"
OUTPUT_DIR="task3_output_${ANCHOR_TEXT}"

JAR_FILE="target/WikiPageRank-1.0-SNAPSHOT.jar"

echo "=================================================="
echo "Running PageRank Task 3: Wikipedia Bomb"
echo "=================================================="
echo "Original links: $LINKS_FILE"
echo "Titles file: $TITLES_FILE"
echo "Target page: $TARGET_PAGE"
echo "Anchor text: $ANCHOR_TEXT"
echo "Modified links output: $OUTPUT_LINKS"
echo "PageRank output: $OUTPUT_DIR"
echo "Iterations: $ITERATIONS"
echo "Beta: $BETA"
echo "=================================================="

# Remove output directories if they exist
if [ -f "$OUTPUT_LINKS" ]; then
    echo "Removing existing modified links file..."
    rm -f "$OUTPUT_LINKS"
fi

if [ -d "$OUTPUT_DIR" ]; then
    echo "Removing existing output directory..."
    rm -rf "$OUTPUT_DIR"
fi

# Run Spark job for WikiBomb
spark-submit \
    --class pagerank.WikiBomb \
    --master local[*] \
    --driver-memory 4g \
    --executor-memory 4g \
    "$JAR_FILE" \
    "$LINKS_FILE" \
    "$TITLES_FILE" \
    "$OUTPUT_LINKS" \
    "$OUTPUT_DIR" \
    "$TARGET_PAGE" \
    "$ANCHOR_TEXT" \
    "$ITERATIONS" \
    "$BETA"

echo ""
echo "Task 3 completed!"
echo "Modified links saved to: $OUTPUT_LINKS"
echo "PageRank results saved to: $OUTPUT_DIR"