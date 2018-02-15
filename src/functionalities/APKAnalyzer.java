package functionalities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.python.core.PyClass;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import models.ApkModel;
import models.ApplicationPermissionsModel;
import models.LibraryModel;
import models.PermissionMethodCallModel;

public class APKAnalyzer {

	public LibraryModel[] getLibrariesPermissions(String apkPath) {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.execfile("mypythonscripts/literadar.py");
		String run="repr(myClass.run(myClass(),'"+apkPath+"'))";
		PyObject str = interpreter.eval(run);
		// System.out.println(str.toString());

		LibraryModel[] libModels = null;
		Gson g = new Gson();
		libModels = g.fromJson(str.toString(), LibraryModel[].class);
		interpreter.close();
		// System.out.println("PRINT "+libModels[0].Library);
		return libModels;
	}

	public ApplicationPermissionsModel getAPKPermissions(String apkPath) throws IOException, InterruptedException {

		Process proc = Runtime.getRuntime().exec("java -jar PermissionChecker.jar " + apkPath);
		proc.waitFor();
		// Then retreive the process output
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();

		byte b[] = new byte[in.available()];
		in.read(b, 0, b.length);
		String data = new String(b);
		// System.out.println("Permissions from .jar:\n"+data);
		Gson g = new Gson();

		models.ApplicationPermissionsModel p = g.fromJson(data, models.ApplicationPermissionsModel.class);
		// System.out.println(p.declared.toString());

		byte c[] = new byte[err.available()];
		err.read(c, 0, c.length);

		return p;

	}
	
	public LibraryModel[] getLibrariesPermissions2(String apkPath) {
		PythonInterpreter pythonInterpreter = new PythonInterpreter();
	 	pythonInterpreter.exec("from literadar import myClass");
        PyClass dividerDef = (PyClass) pythonInterpreter.get("myClass");
        PyObject divider = dividerDef.__call__();
        PyObject pyObject = divider.invoke("run",new PyString("apks/app2.apk"));
        String realResult = pyObject.toString();
        pythonInterpreter.close();
		// System.out.println(str.toString());

		LibraryModel[] libModels = null;
		Gson g = new Gson();
		libModels = g.fromJson(realResult, LibraryModel[].class);
		
		// System.out.println("PRINT "+libModels[0].Library);
		return libModels;
	}
	
	public ApkModel getApkInformation(String apkPath) throws IOException {
		String run="python mypythonscripts/myscript.py -m analyze -f "+apkPath;
		Process p = Runtime.getRuntime().exec(run);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder sb = new StringBuilder();

	    String line = null;
	   
	      while ((line = in.readLine()) != null) {
	    	  //System.out.println(line);
	        sb.append(line + "\n");
	      }

	    String x= sb.toString();
		System.out.println(x);
		Gson g = new Gson();
		ApkModel apk= g.fromJson(x,ApkModel.class);
		return apk;
	}
	
	public ArrayList<PermissionMethodCallModel> getCalls(String apkPath,ArrayList<String> permissionList) throws IOException {
		
		String pList="";
		for (int i=0;i<permissionList.size();i++) {
			pList=pList+permissionList.get(i)+",";
		}

		String run="python mypythonscripts/myscript.py -m call -f "+apkPath+" -p "+pList;
		Process p = Runtime.getRuntime().exec(run);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder sb = new StringBuilder();

	    String line = null;
	   
	      while ((line = in.readLine()) != null) {
	    	  //System.out.println(line);
	        sb.append(line + "\n");
	      }

	    String x= sb.toString();
		//System.out.println(x);
		Gson g = new Gson();
		ArrayList<PermissionMethodCallModel> calls= g.fromJson(x, new TypeToken<ArrayList<PermissionMethodCallModel>>() {
        }.getType());
		
		
		return uniqueList(calls);
		
	}
	
	private ArrayList<PermissionMethodCallModel> uniqueList(ArrayList<PermissionMethodCallModel> list){
		ArrayList<PermissionMethodCallModel> x=new ArrayList<PermissionMethodCallModel>();
		for (PermissionMethodCallModel pmc:list) {
			if(!x.contains(pmc)) {
				x.add(pmc);
			}
			
		}
		return x;
		
		
	}
	
	
	public static void main(String[] args) throws Exception {

		
		ArrayList<String> permissionList= new ArrayList<String>();
		permissionList.add("android.permission.CHANGE_WIFI_STATE");
		permissionList.add("android.permission.ACCESS_WIFI_STATE");
		ArrayList<PermissionMethodCallModel> x=new APKAnalyzer().getCalls("apks/app2.apk", permissionList);
		
		
		
		

		System.out.println(x.toString());
	
		//new APKAnalyzer().getApkInformation("apks/app2.apk");
		
		

		
}
}