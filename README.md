# Normalizing, tokenizing, and clustering webpages.

## Instructions
The code was written and tested using Java 1.8.0_131. [Jsoup](https://jsoup.org/) is used to collect text from URLs.
To run the code, enter the following in a command prompt:

    javac -cp ".;jsoup-1.11.3.jar" ClusterWebPages.java
    
    java -cp ".;jsoup-1.11.3.jar" ClusterWebPages
    
## Steps
1. Collect text from documents located at specified Wikipedia URLs.
2. Remove references and footer information from each document.
3. Remove all non-alphabetical characters and make all characters lower-case.
4. Tokenize each document, splitting by white space.
5. Use hash function to convert each document to a vector.
6. Convert hash counts to [term frequency-inverse document frequency](https://en.wikipedia.org/wiki/Tf%E2%80%93idf) statistics.
7. Cluster samples with [*k*-means clustering](https://en.wikipedia.org/wiki/K-means_clustering), with *k* = 3.
8. Print results.

## Notes
- URLs to be clustered are hard-coded. Articles were chosen such that expected clusters would be obvious: three articles about
dog breeds, three about neurodegenerative diseases, and four about professional football players.
- Using *k*-means to cluster [introduces randomness](https://en.wikipedia.org/wiki/K-means_clustering#Initialization_methods).
This dataset in particular is susceptible to poor clustering due to random centroid initialization because it is so small. 
Centroids used for the first iteration of clustering are chosen by randomly selecting *k* samples from the dataset to serve as 
centroids. Initial centroid selection is seeded so that the algorithm will converge on the proper clusters. To make centroid 
selection random, add (-r "true") to the end of the command above, as follows:

	java -cp ".;jsoup-1.11.3.jar" ClusterWebPages -r "true"
