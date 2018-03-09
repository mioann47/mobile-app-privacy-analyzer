
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



import functionalities.APKAnalyzer;
import functionalities.MalwarePrediction;
import models.ApplicationPermissionsModel;
import models.LibraryModel;
import models.Paths;
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
	
public static final String APKPATH="apks/app2.apk";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		APKAnalyzer apkAnalyzer = new APKAnalyzer();
		
		ApplicationPermissionsModel apm=apkAnalyzer.getAPKPermissions(APKPATH);
		
		
		LibraryModel[] libModels=apkAnalyzer.getLibrariesPermissions(APKPATH);
		
	    ArrayList<String> libsPermissions = new ArrayList<String>();
	    
	    for (int i=0;i<libModels.length;i++) {
	    	libsPermissions.addAll(libModels[i].getPermission());
	    }

	    Set<String> hs = new HashSet<>();
	    hs.addAll(libsPermissions);
	    libsPermissions.clear();
	    libsPermissions.addAll(hs);
	    

	    libsPermissions.removeAll(apm.getDeclared());

	    printlist(apm.getDeclared(),"Permissions declared in apk");
	    printlist(apm.getNotRequiredButUsed(),"Permissions not declared but used in apk");
	    printlist(apm.getRequiredAndUsed(),"Permissions declared and used in apk");
	    printlist(apm.getRequiredButNotUsed(),"Permissions declared and not used in apk");
	    printlist(libsPermissions,"Permissions used by Libraries but not declared in apk");
	    
	    



		Classifier cls = (Classifier) weka.core.SerializationHelper.read(Paths.wekaModelPath);
		
		ArrayList<String> mlist= new ArrayList<String>();
		mlist.addAll(apm.getDeclared());
		mlist.addAll(apm.getNotRequiredButUsed());
		
		MalwarePrediction malpred = new MalwarePrediction(cls, mlist);
		if (malpred.predict()==0)
		System.out.println("Not Malware");
		else System.out.println("Malware");
	    
		ArrayList<String> usedpermissionsList= new ArrayList<String>();
		usedpermissionsList.addAll(apm.getRequiredAndUsed());
		usedpermissionsList.addAll(apm.getNotRequiredButUsed());
		
		apkAnalyzer.getApkInformation(APKPATH);
		ArrayList<PermissionMethodCallModel> callsList=apkAnalyzer.getCalls(APKPATH, usedpermissionsList);
		System.out.println(callsList.toString());
	}

}
