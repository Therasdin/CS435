PA2 – TF-IDF Calculation using Hadoop MapReduce
Overview

This project implements a multi-stage Hadoop MapReduce pipeline to compute TF-IDF (Term Frequency–Inverse Document Frequency) values for words across a dataset of text files.

TF-IDF is a statistical measure used to evaluate how important a word is to a document in a collection or corpus.

The project performs the computation in three stages:

TF (Term Frequency) – Counts how often each word appears in each document.

IDF (Inverse Document Frequency) – Measures how common or rare a word is across all documents.

TF-IDF – Multiplies TF and IDF values to produce the final importance score per word per document.

Input

Folder: /PA2/PA2Dataset

Contents: multiple text files (e.g., doc1.txt, doc2.txt, ...)

Each document represents a separate input file for the TF-IDF pipeline.

Outputs
Job	Output Folder	Description
1	/PA2/output-job1	TF values for each word in each document
2	/PA2/output-job2	IDF values for each word across all documents
3	/PA2/output-job3	Final TF-IDF values (word relevance scores)

Each output directory contains a part-r-00000 file with tab-separated key-value pairs.

Class Descriptions
1. TFMapper.java

Purpose:
Tokenizes input documents and emits (word@document, 1) pairs for each word occurrence.

Important Functions:

map(LongWritable key, Text value, Context context)


Input: document offset (key), document line (value)

Output: (word@document, 1)

Example Output:

word@doc1.txt    1

2. TFCombiner.java

Purpose:
Acts as a local reducer to sum word counts before the shuffle phase, reducing data transfer.

Important Functions:

reduce(Text key, Iterable<IntWritable> values, Context context)


Input: (word@document, [1,1,1,...])

Output: (word@document, count)

Example Output:

word@doc1.txt    4

3. TFReducer.java

Purpose:
Calculates Term Frequency (TF) for each word in each document using total word counts.

Important Functions:

reduce(Text key, Iterable<IntWritable> values, Context context)


Input: (word@document, counts)

Output: (document, word=TF)

Example Output:

doc1.txt    word=0.0833

4. IDFMapper.java

Purpose:
Reads the TF output and emits (word, document) pairs to prepare for document frequency counting.

Important Functions:

map(LongWritable key, Text value, Context context)


Input: (document, word=TF)

Output: (word, document)

5. IDFReducer.java

Purpose:
Computes Inverse Document Frequency (IDF) for each word using the total number of documents.

Formula:

IDF(word) = log10(TotalDocs / DocsContainingWord)


Important Functions:

reduce(Text key, Iterable<Text> values, Context context)


Input: (word, [doc1, doc2, ...])

Output: (word, IDF)

Example Output:

word    0.1761

6. TFIDFMapper.java

Purpose:
Joins TF and IDF outputs and calculates final TF-IDF scores.

Important Functions:

map(LongWritable key, Text value, Context context)


Input: TF data and IDF data

Output: (document, word=TFIDF)

7. TFIDFReducer.java

Purpose:
Produces the final TF-IDF value per word per document by combining TF and IDF.

Important Functions:

reduce(Text key, Iterable<Text> values, Context context)


Input: (word, [TF, IDF])

Output: (document, word=TF-IDF)

Example Output:

doc1.txt    word=0.2388

8. TFIDFDriver.java

Purpose:
Controls the entire pipeline — configures, chains, and runs the three MapReduce jobs in sequence.

Important Functions:

main(String[] args)


Sets up and executes:

TF job → /output-job1

IDF job → /output-job2

TF-IDF job → /output-job3

Example Command:

hadoop jar TFIDF.jar tfidf.TFIDFDriver \
  /PA2/PA2Dataset \
  /PA2/output-job1 \
  /PA2/output-job2 \
  /PA2/output-job3

Example Workflow

Put dataset in HDFS

hdfs dfs -put ~/PA2/PA2Dataset /PA2/PA2Dataset


Run driver
hadoop jar PA2/TFIDF.jar tfidf.TFIDFDriver   /PA2/PA2Dataset  /PA2/output-job1   /PA2/output-job2   /PA2/output-job3

hadoop jar PA2/TFIDF.jar tfidf.TFIDFDriver -D mapreduce.framework.name=yarn /PA2/Demo_dataset.txt /PA2/output-job1 /PA2/output-job2 /PA2/output-job3
Retrieve output

hdfs dfs -get /PA2/output-job1 ~/PA2/output-job1
hdfs dfs -get /PA2/output-job2 ~/PA2/output-job2
hdfs dfs -get /PA2/output-job3 ~/PA2/output-job3

Final Output Example

From /PA2/output-job3/part-r-00000:

doc1.txt    information=0.245
doc1.txt    data=0.180
doc2.txt    mining=0.260

hdfs dfs -rm -r /PA2/output-job#
hdfs dfs -ls /PA2
hdfs dfs -cat /PA2/output-job#/part-r-00000


