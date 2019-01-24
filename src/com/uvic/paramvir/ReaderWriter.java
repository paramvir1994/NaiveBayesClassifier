package com.uvic.paramvir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReaderWriter {
	private List<String> data;
	public ReaderWriter() {
		this.data = new ArrayList<String>();
	}
	
	public List<String> readDataToList(File file) throws FileNotFoundException {
		data.clear();
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			data.add(scanner.nextLine().trim());
		}
		scanner.close();
		return data;
	}

	public void write(List<String> predictedLabels,MultinomialNaiveBayesClassifier classifier) throws IOException {
		FileWriter fw=new FileWriter("resources/results.txt");
		fw.write("Classification Accuracy:"+(classifier.getClassificationAccuracy()*100));
		for(String predictedLabel:predictedLabels) {
			fw.write(predictedLabel+"\n");
		}
		fw.close();
	}
}
