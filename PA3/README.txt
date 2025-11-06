============================================================
Wikipedia PageRank System - README
============================================================

 OVERVIEW
------------------------------------------------------------
This project implements a distributed PageRank system using Apache Spark
to rank Wikipedia articles based on their internal link structure.

Two PageRank algorithms are supported:
  A. Idealized PageRank (no dead ends, no damping)
  B. Taxation-based PageRank (handles dead ends, uses damping factor)

The system processes Wikipedia data dumps and outputs sorted rankings
of articles by importance.

------------------------------------------------------------
 PROJECT STRUCTURE
------------------------------------------------------------

WikipediaPageRank/
│
├── src/
│   └── main/
│       └── java/
│           └── edu/
│               └── csu/
│                   └── pa3/
│                       ├── Main.java
│                       ├── GraphLoader.java
│                       ├── IdealPageRank.java
│                       ├── TaxationPageRank.java
│                       ├── Utils.java
│                       └── constants/
│                           └── Config.java
│
├── data/
│   ├── titles-sorted.txt
│   └── links-simple-sorted.txt
│
├── output/
│   ├── ideal_pagerank.txt
│   └── taxed_pagerank.txt
│
├── pom.xml
└── README.txt
------------------------------------------------------------
 RUNNING THE PROGRAM
------------------------------------------------------------



------------------------------------------------------------
 ALGORITHM DETAILS
------------------------------------------------------------

A. Idealized PageRank
   - Assumes every article has outbound links
   - No damping factor
   - Rank is distributed evenly across outbound links

B. Taxation-based PageRank
   - Uses damping factor (default: 0.85)
   - Handles dead-end articles by redistributing their rank
   - Simulates random jumps across the graph

Both algorithms run for 25 iterations by default.

------------------------------------------------------------
 KEY CLASSES & METHODS
------------------------------------------------------------

Main.java
  - Entry point, orchestrates loading, computation, and saving

GraphLoader.java
  - loadTitles(path): RDD of (articleId, title)
  - loadLinks(path): RDD of (sourceId, List<targetIds>)

IdealPageRank.java
  - computePageRank(links, iterations): RDD of (articleId, rank)

TaxationPageRank.java
  - computePageRank(links, iterations, damping): RDD of (articleId, rank)

Utils.java
  - saveResults(results, path): writes formatted output
  - formatTitle(title): cleans up underscores
  - formatRank(rank): formats to 6 decimal places

Config.java
  - DEFAULT_DAMPING_FACTOR = 0.85
  - DEFAULT_ITERATIONS = 25
------------------------------------------------------------
TEST RESULTS SHOULD BE
------------------------------------------------------------
PageA    0.3125
PageB    0.2812
PageC    0.2187
PageD    0.1875
