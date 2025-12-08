# Song Genre Classification & Recommendation System
---

## Overview
A standalone Java application that classifies songs by genre using lyrics and recommends similar tracks.  
It uses TF‑IDF for text representation, Multinomial Naive Bayes for classification, and Cosine Similarity for recommendations.  
No Spark or external ML libraries are required. Compatible with Hadoop MapReduce.

---

## Features
- CSV parsing with support for quoted lyrics and commas  
- Song data model for clean handling of metadata  
- TF‑IDF + Naive Bayes classifier  
- TF‑IDF + cosine similarity recommender  
- End‑to‑end pipeline: Load → Train → Predict → Recommend  
- Command‑line execution  

---

## File Structure
FinalProject/  
├── PredictAndRecommend.java   (Main program)  
├── Song.java                  (Song model)  
├── SongCSVParser.java         (CSV utility)  
├── SongGenreClassifier.java   (Classifier)  
├── SongRecommender.java       (Recommender)  
├── RecordReader.java          (Optional Hadoop mapper)  
└── README.md  

---

## Dataset Format
CSV rows should include:

Index | Field  
0     | Title  
1     | Genre  
2     | Artist  
3     | Year  
4     | Views  
5     | Features  
6     | Lyrics  
7     | Song ID  
10    | Language  

Only rows with non‑empty lyrics are used for training.

---

## Compile
javac FinalProject/*.java

---
## Move everything to the right director 
cd ~/PA3/CS435/FinalProject/src/main/java
mkdir FinalProject
mv *.java FinalProject/

## Download required JARs
wget https://repo1.maven.org/maven2/com/opencsv/opencsv/5.9/opencsv-5.9.jar
wget https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar

## Compile all Java files
javac -cp .:opencsv-5.9.jar:commons-lang3-3.14.0.jar FinalProject/*.java

## Run using default lyrics or dataset
java -cp .:opencsv-5.9.jar:commons-lang3-3.14.0.jar FinalProject.PredictAndRecommend ../../../song_lyrics.csv

## Run with custom lyrics
java -cp .:opencsv-5.9.jar:commons-lang3-3.14.0.jar FinalProject.PredictAndRecommend ../../../song_lyrics.csv "walking down this lonely road"


## Run
java FinalProject.PredictAndRecommend songs.csv

Optional: provide custom lyrics for prediction  
java FinalProject.PredictAndRecommend songs.csv "walking down this lonely road"

---

## Output
- Loads dataset  
- Trains classifier  
- Predicts genre of new input  
- Shows top 5 recommended songs  

Example:  
Loaded 12000 songs.  
Predicted genre: rock  

Top 5 recommendations:  
* Song A by Artist X [rock]  
* Song B by Artist Y [rock]  
* Song C by Artist Z [alternative]  

---

## Algorithms
Classification: TF‑IDF, Multinomial Naive Bayes, log‑priors, Laplace smoothing  
Recommendation: TF‑IDF vectors, cosine similarity, content‑based filtering  

---

## Requirements
- Java 17+  
- OpenCSV library  
- No Spark or external ML libraries  

---

## Notes
- Deterministic, no model persistence needed  
- Operates fully in memory  
- Suitable for demos, research, and academic use  

---

## Academic Integrity
Developed as a final project using standard ML techniques implemented in Java.

---
