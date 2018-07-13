import java.util.Arrays;
import java.lang.Math;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class ClusterWebPages {

	/**
	 * This method clusters web pages with the following steps:
	 *	1. Read text from document at each URL
	 * 	2. Normalize the text of each document
	 *	3. Tokenize the text of each document
	 * 	4. Weight tokens according to tf-idf
	 *	5. Cluster via k-means, k = 3
	 *
	 */
	public static void main(String[] args) {

		Boolean rand = false;
		if (args.length > 0 && args[1].equals("true")) {
			rand = true;
		} 

		// URLs of documents to be clustered
		String[] urls = new String[] {
			"https://en.wikipedia.org/wiki/Golden_Retriever",
			"https://en.wikipedia.org/wiki/German_Shepherd",
			"https://en.wikipedia.org/wiki/Great_Dane",
			"https://en.wikipedia.org/wiki/Alzheimer's_disease",
			"https://en.wikipedia.org/wiki/Parkinson's_disease",
			"https://en.wikipedia.org/wiki/Huntington's_disease",
			"https://en.wikipedia.org/wiki/Lionel_Messi",
			"https://en.wikipedia.org/wiki/Cristiano_Ronaldo",
			"https://en.wikipedia.org/wiki/Harry_Kane",
			"https://en.wikipedia.org/wiki/David_Beckham"
		};

		// Number of clusters to use
		int numClusters = 3;

		// Create object to hold tokenized documents
		int numSamples = urls.length;
		int numFeatures = (int) Math.pow(2, 16);
		double[][] vectorizedPages = new double[numSamples][numFeatures];

		// Process each URL to prepare for clustering
		for (int i = 0; i < numSamples; i++) {
			Document doc = null;
	    	try {
				doc = Jsoup.connect(urls[i]).get();
			} catch (IOException e) {
				System.out.println("Final RIP:");
			}

			String text = doc.body().text();

			// Remove references and document footer
			String[] textSplit = text.split("References");
			text = textSplit[0] + textSplit[1];

			// Normalize and tokenize
			text = normalizeText(text);
			String[] tokens = tokenizeText(text);

			// Hash each word
			for (String token: tokens) {
				vectorizedPages[i][hashWord(token, numFeatures)] += 1;
			}
		}

		// Convert raw counts to tf-idf statistics
		vectorizedPages = computeTfIdf(vectorizedPages, numSamples, numFeatures);

		// Perform clustering
	  	KMeans kMeans = new KMeans();
		int[] clusters = kMeans.kMeans(vectorizedPages, numClusters, rand);

		// Print results
		System.out.println("");
		for (int i = 0; i < numClusters; i++) {
			System.out.println("Cluster " + Integer.toString(i+1) + ":");
			for (int j = 0; j < numSamples; j++) {
				if (clusters[j] == i) {
					String[] titleSplit = urls[j].split("/")[4].split("_");
					String title = titleSplit[0] + " " + titleSplit[1];
					System.out.println(title);
				}
			}
			System.out.println("");
		}
    }


    /**
	 * This method computes the tf-idf statistic for each feature of each
	 * sample. 
	 *
	 * @param data Hashed input data (numSamples, numFeatures)
	 * @param numSamples Number of samples
	 * @param numFeatures Number of features per sample
	 *
	 * @return double[][] 2D array with hashes weight by tf-idf
	 *
	 */
    private static double[][] computeTfIdf(double[][] data, int numSamples, int numFeatures) {
    	// Number of documents each hash appears in
    	double[] docFrequency = new double[numFeatures];
    	
    	// Number of words in each document
    	double[] wordCount = new double[numSamples];

    	for (int row = 0; row < numSamples; row++) {
    		for (int col = 0; col < numFeatures; col++) {
    			wordCount[row] += data[row][col];
    			if (data[row][col] > 0) {
    				docFrequency[col] += 1;
    			}
    		}
    	}

    	// Iterate through each sample and feature
    	for (int row = 0; row < numSamples; row++) {
    		for (int col = 0; col < numFeatures; col++) {
    			// Skip absent entries
    			if (data[row][col] == 0) {
    				continue;
    			}

    			// Calculate tf-idf for the feature/sample pair
    			data[row][col] = (data[row][col] / wordCount[row]) * Math.log(data[row][col] / numSamples);
    		}
    	}

    	return data;
    }


    /**
	 * This method hashes a string into an integer value.
	 *
	 * @param word String to be hashed
	 * @param maxHash Upper range of possible output values [0, maxHash)
	 *
	 * @return int Hash of provided string
	 *
	 */
    private static int hashWord(String word, int maxHash) {
    	int hash = 7;
    	for (int i = 0; i < word.length(); i++) {
    		hash = hash * 17 + word.charAt(i);
    	}

    	return Math.abs(hash % maxHash);
    }


	/**
	 * This method normalizes the text by expanding basic contractions,
	 * removing all non-alphabetical characters, and making all characters
	 * lower-case.
	 *
	 * @param text String to be normalized
	 *
	 * @return String normalized text
	 *
	 */
    private static String normalizeText(String text) {
    	// Expand basic contractions
    	text = text.replaceAll("n't", " not");
	    text = text.replaceAll("'re", " are");
	    text = text.replaceAll("'m", " am");
	    text = text.replaceAll("'ll", " will");
	    text = text.replaceAll("'ve", " have");

	    // Only keep alphabetical characters, make lower-case
	    text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();

	    return text;
    }


    /**
	 * This method tokenizes the input text.
	 *
	 * @param text Input string, multiple sentences
	 *
	 * @return String[] Words separated as tokens to be used as features
	 *
	 */
    private static String[] tokenizeText(String text) {
    	return text.split("\\s+");
    }
}