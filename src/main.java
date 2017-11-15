import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import models.LibraryModel;



public class main {

	
	public static String getJSON(String url, int timeout) {
	    HttpURLConnection c = null;
	    try {
	        URL u = new URL(url);
	        c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        //c.setConnectTimeout(timeout);
	        //c.setReadTimeout(timeout);
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
	
	
	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	    Process proc = Runtime.getRuntime().exec("java -jar PermissionChecker.jar apks/org.adaway_60.apk");
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
	    
	    String jdata = getJSON("http://192.168.1.100:5000/apk",0);
	    //System.out.println(jdata);
	    
	    LibraryModel[] array = g.fromJson(jdata, LibraryModel[].class);
	    
	    //System.out.println("Test="+array[1].Popularity);
	    //System.out.println("Test="+array[3].Popularity);
	    System.out.println("Test="+array[0].Permission.toString());
	}

}
