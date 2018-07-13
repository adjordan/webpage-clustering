import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import java.lang.Math;


public class KMeans {

	Boolean isRandom;

	/**
	 * This method clusters provided data into specified number
	 * of clusters. 
	 *
	 * @param data Input data (numSamples, numFeatures)
	 * @param numClusters Number of clusters in which samples should be grouped
	 * @param rand Determines if intial centroid selection is random or not.
	 *
	 * @return int[] array indicating cluster for each sample. Indices match rows
	 *		   of input data
	 *
	 */
	public int[] kMeans(double[][] data, int numClusters, Boolean rand) {
		// Set flag for centroid selection
		isRandom = rand;

		// Count number of samples and initialize array to hold cluster labels
		int numSamples = data.length;
		int[] clusters = new int[numSamples];
		
		// Randomly select samples as initial centroids
		double[][] initialCentroids = getInitialCentroids(data, numClusters);

		// Get initial clusters
		clusters = generateClusters(data, initialCentroids);

		// Iterate until convergence
		int[] clustersOld = new int[numSamples];
		while (!Arrays.equals(clusters, clustersOld)) {

			// Calculate new centroids
			double[][] centroids = calculateCentroids(data, clusters, numClusters);
			
			// Re-calculate clusters with new centroids
			clustersOld = clusters;
			clusters = generateClusters(data, centroids);
		}

		return clusters;
	}


	/**
	 * This method calculates centroids for set of samples that
	 * has been clustered.
	 *
	 * @param data Input data (numSamples, numFeatures)
	 * @param clusters Array indicating which cluster each sample belongs to (1, numSamples)
	 * @param numClusters Number of centroids to select
	 *
	 * @return double[][] Centroids (k, numFeatures)
	 *
	 */
	private double[][] calculateCentroids(double[][] data, int[] clusters, int numClusters) {
		// Determine how many samples are in each cluster
		int[] numSamplesPerCluster = getNumSamplesPerCluster(clusters, numClusters);

		// Calculate centroids based on clusters
		double[][] centroids = new double[numClusters][data[0].length];
		for (int i = 0; i < clusters.length; i++) {

			for (int j = 0; j < centroids[clusters[i]].length; j++) {
				centroids[clusters[i]][j] = centroids[clusters[i]][j] + (data[i][j] / numSamplesPerCluster[clusters[i]]);
			}
		}

		return centroids;
	}


	/**
	 * This method calculates the euclidean distance between two arrays.
	 *
	 * @param arr1 First array (1, numFeatures)
	 * @param arr2 Second array (k, numFeatures)
	 * @param numClusters Number of centroids to select
	 *
	 * @return int Cluster assignment for each sample
	 *
	 */
	private double calculateEuclideanDistance(double[] arr1, double[] arr2) {
		double sum = 0.0;
		for (int i = 0; i < arr1.length; i++) {
			sum  = sum + Math.pow(arr1[i]-arr2[i], 2);
		}

		return Math.sqrt(sum);
	}


	/**
	 * This method determines which centroid a provided sample is
	 * closest to.
	 *
	 * @param sample Input sample (1, numFeatures)
	 * @param centroids Candidate centroids (k, numFeatures)
	 *
	 * @return int Label of centroid that the provided sample is closest to
	 *
	 */
	private int calculateNearestCentroid(double[] sample, double[][] centroids) {
		double minDist = Double.POSITIVE_INFINITY;
		int closestCentroid = -1;
		for (int i = 0; i < centroids.length; i++) {
			double dist = calculateEuclideanDistance(sample, centroids[i]);
			if (dist < minDist)
			{
				minDist = dist;
				closestCentroid = i;
			}
		}

		return closestCentroid;
	}


	/**
	 * This method clusters the data around the provided centroids.
	 *
	 * @param data Input data to be clustered (numSamples, numFeatures)
	 * @param centroids Centroids to be clustered around (k, numFeatures)
	 *
	 * @return int[] Cluster assignment for each sample (1, numSamples)
	 *
	 */
	private int[] generateClusters(double[][] data, double[][] centroids) {
		int[] clusters = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			clusters[i] = calculateNearestCentroid(data[i], centroids);
		}

		return clusters;
	}

	
	/**
	 * This method randomly selects samples from the input data
	 * to serve as initial clusters for k-means clustering.
	 *
	 * @param data Input data (numSamples, numFeatures)
	 * @param numClusters Number of centroids to select
	 *
	 * @return double[][] Selected centroids (k, numFeatures)
	 *
	 */
	private double[][] getInitialCentroids(double[][] data, int numClusters) {
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < data.length; i++) {
            list.add(new Integer(i));
        }
		
		int[] selectedSamples = new int[numClusters];
		if (isRandom) {
			Collections.shuffle(list);
		} else {
			Collections.shuffle(list, new Random(3));
		}
        
        for (int i = 0; i < numClusters; i++) {
            selectedSamples[i] = list.get(i);
        }

        // selectedSamples[0] = 0;
        // selectedSamples[1] = 4;
        // selectedSamples[2] = 5;

        double[][] centroids = new double[numClusters][data[0].length];
        for (int i = 0; i < selectedSamples.length; i++) {
        	centroids[i] = data[selectedSamples[i]];
        }
		
		return centroids;
	}


	/**
	 * This method clusters the data around the provided centroids.
	 *
	 * @param clusters Array indicating which cluster each sample belongs to (1, numSamples)
	 * @param numClusters Number of centroids to select
	 *
	 * @return int Array containing the number of samples in each cluster
	 *
	 */
	private int[] getNumSamplesPerCluster(int[] clusters, int numClusters) {
		int[] occurences = new int[numClusters];
		for (int i = 0; i < clusters.length; i++) {
			occurences[clusters[i]] += 1;
		}

		return occurences;
	}

}