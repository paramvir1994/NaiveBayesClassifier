package com.uvic.paramvir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class App {
	public static File trainDataFile = new File("resources/traindata.txt");
	public static File trainLabelsFile=new File("resources/trainlabels.txt");
	public static File testDataFile=new File("resources/testdata.txt");
	public static File testLabelsFile=new File("resources/testlabels.txt");
	
	public static void main(String[] args) {	
		try {
			Trainer trainer=new Trainer(trainDataFile,trainLabelsFile);
			trainer.train();
			MultinomialNaiveBayesClassifier classifier=new MultinomialNaiveBayesClassifier(testDataFile,testLabelsFile,trainer);
			List<String> classifiedLabels=classifier.classify();
			ReaderWriter rw = new ReaderWriter();
			rw.write(classifiedLabels,classifier);
			System.out.println("Processed!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
