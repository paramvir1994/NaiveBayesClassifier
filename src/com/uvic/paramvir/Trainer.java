package com.uvic.paramvir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Trainer {
	
	private ReaderWriter rw = null;

	/**
	 * Set of unique terms in the training data set
	 */
	private Set<String> vocabularySet;
	
	/**
	 * File containing Training data
	 */
	private File trainDataFile;
	
	/**
	 * File containing Training labels
	 */
	private File trainLabelsFile;
	
	/**
	 * List of training labels Label for ith training example at ith index
	 */
	private List<String> labels;
	
	/**
	 * Map containing number of documents for each label (Label,Count)----
	 * (key,value)
	 */
	private HashMap<String, Integer> numberOfDocsPerLabelMap;
	
	/**
	 * A 2 dimensional hashmap First dimension contains (label,hashmap) pairs where
	 * the hashmap as value is the count of each term per label
	 */
	private HashMap<String, HashMap<String, Integer>> processedVocabularyMap;
	
	/**
	 * A 2 dimensional hashmap First dimension contains(label,hashmap) pairs where
	 * the hashmap as value is the conditional probability of each term wrt label
	 * P(term=?/label=?) can be found using conditionalProb.get(label).get(term)
	 */
	private HashMap<String, HashMap<String, Double>> conditionalProbabilityMap;

	/**
	 * priorProb represents P(C=?) => Number of examples with label=? / total no. of
	 * examples
	 */
	private HashMap<String, Double> priorProbabilityMap;

	
	public HashMap<String, HashMap<String, Double>> getConditionalProb() {
		return this.conditionalProbabilityMap;
	}

	public HashMap<String, Double> getPriorProbability() {
		return this.priorProbabilityMap;
	}

	public Set<String> getVocabulary() {
		return this.vocabularySet;
	}

	public Set<String> getUniqueLabels() {
		return this.processedVocabularyMap.keySet();
	}

	public Trainer(File traindataFile, File trainlabelsFile) {
		this.rw = new ReaderWriter();
		this.trainDataFile = traindataFile;
		this.trainLabelsFile = trainlabelsFile;
		this.labels = new ArrayList<String>();
		this.vocabularySet = new HashSet<String>();
		this.numberOfDocsPerLabelMap = new HashMap<String, Integer>();
		this.priorProbabilityMap = new HashMap<String, Double>();
		this.processedVocabularyMap = new HashMap<String, HashMap<String, Integer>>();
		this.conditionalProbabilityMap = new HashMap<String, HashMap<String, Double>>();
	}

	public void train() {
		try {
			labels = rw.readDataToList(trainLabelsFile);
			extractVocabulary(trainDataFile);
			countDocsForEachLabel();
			calculateConditionalProbability();
			calculatePriorProbability();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
	}

	private void calculatePriorProbability() {
		Set<String> distinctLabels = processedVocabularyMap.keySet();	//1,0
		for (String label : distinctLabels) {
			priorProbabilityMap.put(label, numberOfDocsPerLabelMap.get(label) * 1.0 / labels.size());
		}
	}

	private void calculateConditionalProbability() {
		conditionalProbabilityMap.clear();
		long frequencyOfAllTermsPerLabel = 0;
		long frequencyOfIndividualTermsPerLabel = 0;
		Set<String> distinctLabels = processedVocabularyMap.keySet();	//1,0
		for (String label : distinctLabels) {
			frequencyOfAllTermsPerLabel = 0;
			for (long frequency : processedVocabularyMap.get(label).values()) {
				frequencyOfAllTermsPerLabel += frequency;
			}
			conditionalProbabilityMap.put(label, new HashMap<String, Double>());
			for (String term : vocabularySet) {
				if (processedVocabularyMap.get(label).get(term) != null)
					frequencyOfIndividualTermsPerLabel = processedVocabularyMap.get(label).get(term);
				else
					frequencyOfIndividualTermsPerLabel = 0;

				conditionalProbabilityMap.get(label).put(term, (frequencyOfIndividualTermsPerLabel + 1) * 1.0
						/ (frequencyOfAllTermsPerLabel + vocabularySet.size()));
			}
		}
	}

	/**
	 * Counting documents for each label
	 * 
	 */
	private void countDocsForEachLabel() {
		numberOfDocsPerLabelMap.clear();
		for (String label : labels) {
			if (numberOfDocsPerLabelMap.containsKey(label)) {
				numberOfDocsPerLabelMap.put(label, numberOfDocsPerLabelMap.get(label) + 1);
			} else {
				numberOfDocsPerLabelMap.put(label, 1);
			}
		}
	}

	private void extractVocabulary(File trainDataFile) throws FileNotFoundException {
		processedVocabularyMap.clear();
		vocabularySet.clear();
		Scanner scanner = new Scanner(trainDataFile);
		String[] termsInDocument;
		int documentId = 0;
		while (scanner.hasNextLine()) {
			termsInDocument = scanner.nextLine().split(" ");
			for (String term : termsInDocument) {
				addTermToVocabulary(term, documentId);
			}
			documentId++;
		}
		scanner.close();
	}

	private void addTermToVocabulary(String term, int documentId) {
		vocabularySet.add(term);
		String label = labels.get(documentId);
		if (processedVocabularyMap.containsKey(label) == false) {
			processedVocabularyMap.put(label, new HashMap<String, Integer>());
		}
		if (processedVocabularyMap.get(label).containsKey(term)) {
			processedVocabularyMap.get(label).put(term, processedVocabularyMap.get(label).get(term) + 1);
		} else {
			processedVocabularyMap.get(label).put(term, 1);
		}
	}
}
