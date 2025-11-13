#!/bin/bash

LINKS=$1
TITLES=$2
OUTPUT=$3
ITERATIONS=${4:-25}

spark-submit \
    --class edu.csu.pa3.Main \
    --master local[*] \
    --driver-memory 4g \
    target/WikiPageRank-1.0-SNAPSHOT.jar \
    $TITLES \
    $LINKS \
    $OUTPUT \
    false