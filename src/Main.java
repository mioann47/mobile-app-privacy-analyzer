import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

import functionalities.APKAnalyzer;
import models.ApplicationPermissionsModel;
import models.LibraryModel;



public class Main {

	
	
	
	public static void printlist(ArrayList<String> list,String title) {
	    System.out.println("\n***"+title+":***");
	    for (int i=0;i<list.size();i++) {
	    	 System.out.println(list.get(i));
	    }
		
	}
public static final boolean WEBMODE=true;	
public static final String APKPATH="apks/app2.apk";
public static final String serverIPandPort = "http://172.20.240.45:5000/apk";
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		APKAnalyzer apkAnalyzer = new APKAnalyzer();
		
		ApplicationPermissionsModel apm=apkAnalyzer.getAPKPermissions(APKPATH);
		//System.out.println(apm.declared.toString());
		Gson g= new Gson();
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
	    printlist(apm.requiredAndUsed,"Permissions declared and not used in apk");
	    printlist(libsPermissions,"Permissions used by Libraries but not declared in apk");
	    
	}

}
