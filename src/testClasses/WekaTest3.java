package testClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class WekaTest3 {

	public static void main(String[] args) throws Exception {

		Instances myAttributes = new Instances(new BufferedReader(new FileReader("Datasets/myAttributes.arff")));
		int attributesNumber = myAttributes.numAttributes();
		myAttributes.setClassIndex(attributesNumber - 1);

		ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
		for (int i = 0; i < attributesNumber; i++) {
			attributeList.add(myAttributes.attribute(i));
		}

		Instances ApkInstance = new Instances("apkInstance", attributeList, 331);
		ApkInstance.setClassIndex(attributesNumber - 1);
	

		
		
		Instance iExample = new DenseInstance(attributesNumber);
		
		for (int i=0;i<attributesNumber-1;i++) {
			iExample.setValue((Attribute)attributeList.get(i), 0);
		}
		iExample.setValue((Attribute)attributeList.get(attributesNumber-1), 1);
		ApkInstance.add(iExample);
		
		
		Classifier cls = (Classifier) weka.core.SerializationHelper.read("myModel.model");
	
		double actualclass = ApkInstance.instance(0).classValue();
		String actual = ApkInstance.classAttribute().value((int) actualclass);
		double prednbk = cls.classifyInstance(ApkInstance.instance(0));
		String predString = ApkInstance.classAttribute().value((int) prednbk);

		System.out.println("Actual : "+actual+" Predicted : "+predString);
/*
		for (int i = 0; i < test.numInstances(); i++) {

			double actualclass = test.instance(i).classValue();
			String actual = test.classAttribute().value((int) actualclass);
			Instance newInst = test.instance(i);
			double prednbk = cls.classifyInstance(newInst);
			String predString = test.classAttribute().value((int) prednbk);

			// System.out.println("Actual : "+actual+" Predicted : "+predString);

		}*/

	}
}