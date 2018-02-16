
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



import functionalities.APKAnalyzer;
import functionalities.MalwarePrediction;
import models.ApplicationPermissionsModel;
import models.LibraryModel;
import models.PermissionMethodCallModel;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;



public class Main {

	
	
	
	public static void printlist(ArrayList<String> list,String title) {
	    System.out.println("\n***"+title+":***");
	    for (int i=0;i<list.size();i++) {
	    	 System.out.println(list.get(i));
	    }
		
	}
	
public static final String APKPATH="apks/app3.apk";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		APKAnalyzer apkAnalyzer = new APKAnalyzer();
		
		ApplicationPermissionsModel apm=apkAnalyzer.getAPKPermissions(APKPATH);
		
		
		LibraryModel[] libModels=apkAnalyzer.getLibrariesPermissions(APKPATH);
		
	    ArrayList<String> libsPermissions = new ArrayList<String>();
	    
	    for (int i=0;i<libModels.length;i++) {
	    	libsPermissions.addAll(libModels[i].Permission);
	    }

	    Set<String> hs = new HashSet<>();
	    hs.addAll(libsPermissions);
	    libsPermissions.clear();
	    libsPermissions.addAll(hs);
	    

	    libsPermissions.removeAll(apm.declared);

	    printlist(apm.declared,"Permissions declared in apk");
	    printlist(apm.notRequiredButUsed,"Permissions not declared but used in apk");
	    printlist(apm.requiredAndUsed,"Permissions declared and used in apk");
	    printlist(apm.requiredButNotUsed,"Permissions declared and not used in apk");
	    printlist(libsPermissions,"Permissions used by Libraries but not declared in apk");
	    
	    
	    Instances myAttributes = new Instances(new BufferedReader(new FileReader("Datasets/myAttributes.arff")));
		int attributesNumber = myAttributes.numAttributes();
		myAttributes.setClassIndex(attributesNumber - 1);

		ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
		for (int i = 0; i < attributesNumber; i++) {
			attributeList.add(myAttributes.attribute(i));
		}


		Classifier cls = (Classifier) weka.core.SerializationHelper.read("myModel.model");
		
		ArrayList<String> mlist= new ArrayList<String>();
		mlist.addAll(apm.declared);
		mlist.addAll(apm.notRequiredButUsed);
		
		MalwarePrediction malpred = new MalwarePrediction(cls, mlist, attributeList,attributesNumber);
		if (malpred.predict()==0)
		System.out.println("Not Malware");
		else System.out.println("Malware");
	    
		ArrayList<String> usedpermissionsList= new ArrayList<String>();
		usedpermissionsList.addAll(apm.requiredAndUsed);
		usedpermissionsList.addAll(apm.notRequiredButUsed);
		
		apkAnalyzer.getApkInformation(APKPATH);
		ArrayList<PermissionMethodCallModel> callsList=apkAnalyzer.getCalls(APKPATH, usedpermissionsList);
		System.out.println(callsList.toString());
	}

}
