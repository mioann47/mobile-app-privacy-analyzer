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
import java.util.logging.Level;
import java.util.logging.Logger;

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
	    System.out.println(data);
	    Gson g= new Gson();
	   
	    models.ApplicationPermissionsModel p = g.fromJson(data, models.ApplicationPermissionsModel.class);
	    //System.out.println(p.declared.toString());

	    byte c[]=new byte[err.available()];
	    err.read(c,0,c.length);
	    
	    return p;
		
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		ApplicationPermissionsModel apm=getPermissions("apks/org.adaway_60.apk");
		//System.out.println(apm.declared.toString());
		
	    //String jdata = getJSON("http://192.168.1.100:5000/apk");
	    //System.out.println(jdata);
	    Gson g= new Gson();
	    //LibraryModel[] libArray = g.fromJson(jdata, LibraryModel[].class);
	    
	    //System.out.println("Test="+array[1].Popularity);
	    //System.out.println("Test="+array[3].Popularity);
	    //System.out.println("Test="+array[0].Permission.toString());
	    String JSON_PATH = "apk.json";

	    Gson gson = new Gson();
	    BufferedReader br = new BufferedReader(new FileReader(JSON_PATH));
	    LibraryModel[] libModels = g.fromJson(br, LibraryModel[].class);
	    //System.out.println("Test="+libModels[0].Permission.toString());
	    
	    ArrayList<String> libsPermissions = new ArrayList<String>();
	    
	    for (int i=0;i<libModels.length;i++) {
	    	libsPermissions.addAll(libModels[i].Permission);
	    }
	    //System.out.println(libsPermissions.toString());
	    Set<String> hs = new HashSet<>();
	    hs.addAll(libsPermissions);
	    libsPermissions.clear();
	    libsPermissions.addAll(hs);
	    
	    //System.out.println(libsPermissions.toString());
	    libsPermissions.removeAll(apm.declared);
	    System.out.println(libsPermissions.toString());
	    
	}

}
