package com.uvic.paramvir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class MultinomialNaiveBayesClassifier {
	private File testDataFile;
	private File testLabelsFile;
	/**
	 * Contains probability of each label for any test example
	 * The label with the maximum probability is eventually made the predicted label
	 */
	private HashMap<String,Double> probLabel;
	/**
	 * List of predicted labels for the test data
	 */
	private List<String> predictedLabels;
	private Trainer trainer;
	
	MultinomialNaiveBayesClassifier(File testDataFile,File testLabelsFile,Trainer trainer){
		this.testDataFile=testDataFile;
		this.testLabelsFile=testLabelsFile;
		this.trainer=trainer;
		this.predictedLabels=new ArrayList<String>();
		this.probLabel=new HashMap<String,Double>();
	}
	
	public List<String> classify() throws FileNotFoundException {
		Scanner scData=new Scanner(testDataFile);
		while(scData.hasNextLine()) {
			findProbLabels(scData.nextLine().split(" "));
			predictedLabels.add(predictLabel());	
		}
		scData.close();
		return this.predictedLabels;
	}
	
	private void findProbLabels(String[] document) {
		probLabel.clear();
		for(String label:trainer.getUniqueLabels()) {
			probLabel.put(label, trainer.getPriorProbability().get(label));
			for(String term:document) {
				if(trainer.getVocabulary().contains(term)) {
					probLabel.put(label, 
							probLabel.get(label)
							*trainer.getConditionalProb().get(label).get(term));
				}
				
			}
		}
	}
	
	private String predictLabel() {
		double maxProb=0;
		String predictedLabel="";
		for(String label:trainer.getUniqueLabels()) {
			if(probLabel.get(label)>maxProb) {
				maxProb=probLabel.get(label);
				predictedLabel=label;
			}
		}
		return predictedLabel;
	}
	
	public double getClassificationAccuracy() throws FileNotFoundException {
		int correctClassified=0;
		int m=predictedLabels.size();
		Scanner sc=new Scanner(testLabelsFile);
		for(int i=0;i<m;i++) {
			if(predictedLabels.get(i).equals(sc.nextLine().trim())) {
				correctClassified++;
			}
		}
		double accuracy=correctClassified*1.0/m;
		sc.close();
		return accuracy;
	}
	
}
