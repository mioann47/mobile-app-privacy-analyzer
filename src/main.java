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

import models.ApplicationPermissionsModel;
import models.LibraryModel;



public class main {

	
	public static String getJSON(String url) {
	    HttpURLConnection c = null;
	    try {
	        URL u = new URL(url);
	        c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        c.connect();
	        int status = c.getResponseCode();

	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                return sb.toString();
	        }

	    } catch (MalformedURLException ex) {
	        //Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException ex) {
	        //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } finally {
	       if (c != null) {
	          try {
	              c.disconnect();
	          } catch (Exception ex) {
	             //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	          }
	       }
	    }
	    return null;
	}
	
	
	public static ApplicationPermissionsModel getPermissions(String apkPath) throws IOException, InterruptedException {
		
	    Process proc = Runtime.getRuntime().exec("java -jar PermissionChecker.jar "+apkPath);
	    proc.waitFor();
	    // Then retreive the process output
	    InputStream in = proc.getInputStream();
	    InputStream err = proc.getErrorStream();

	    byte b[]=new byte[in.available()];
	    in.read(b,0,b.length);
	    String data = new String(b);
	   // System.out.println("Permissions from .jar:\n"+data);
	    Gson g= new Gson();
	   
	    models.ApplicationPermissionsModel p = g.fromJson(data, models.ApplicationPermissionsModel.class);
	    //System.out.println(p.declared.toString());

	    byte c[]=new byte[err.available()];
	    err.read(c,0,c.length);
	    
	    return p;
		
	}
	
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

		ApplicationPermissionsModel apm=getPermissions(APKPATH);
		//System.out.println(apm.declared.toString());
		Gson g= new Gson();
		LibraryModel[] libModels;
		if (WEBMODE) {
	    String jdata = getJSON(serverIPandPort);
	    //System.out.println(jdata);
	    
	    libModels = g.fromJson(jdata, LibraryModel[].class);
	    

		}else {
	    String JSON_PATH = "apk.json";
	    

	    
	    BufferedReader br = new BufferedReader(new FileReader(JSON_PATH));
	    libModels = g.fromJson(br, LibraryModel[].class);
		
		}
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
