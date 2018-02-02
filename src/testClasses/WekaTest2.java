package testClasses;
import weka.classifiers.lazy.IBk;
import java.io.BufferedReader;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public class WekaTest2 {

    public static void main(String[] args) throws Exception {

            Classifier iBk = new IBk(1);
            
            Instances train = new Instances(new BufferedReader(new FileReader("Datasets/train.arff")));
            int lastIndex = train.numAttributes() - 1;
            
            train.setClassIndex(lastIndex);
            
            Instances test = new Instances(new BufferedReader(new FileReader("Datasets/test.arff")));
            test.setClassIndex(lastIndex);
            
            iBk.buildClassifier(train);
            
            

          
          
            weka.core.SerializationHelper.write("myModel.model", iBk);
            Classifier cls = (Classifier) weka.core.SerializationHelper.read("myModel.model");
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(cls, test);
            System.out.println(eval.toSummaryString()); ;
            for(int i=0; i<test.numInstances(); i++) {
        		
            	double actualclass=test.instance(i).classValue();
            	String actual = test.classAttribute().value((int)actualclass);
            	Instance newInst=test.instance(i);
            	double prednbk = iBk.classifyInstance(newInst);
            	String predString=test.classAttribute().value((int)prednbk);

                System.out.println("Actual : "+actual+" Predicted : "+predString);

        }


            
    }
}