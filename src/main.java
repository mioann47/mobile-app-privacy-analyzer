import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;



public class main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	    Process proc = Runtime.getRuntime().exec("java -jar PermissionChecker.jar app.apk");
	    proc.waitFor();
	    // Then retreive the process output
	    InputStream in = proc.getInputStream();
	    InputStream err = proc.getErrorStream();

	    byte b[]=new byte[in.available()];
	    in.read(b,0,b.length);
	    String data = new String(b);
	    Gson g= new Gson();
	   
	    models.ApplicationPermissionsModel p = g.fromJson(data, models.ApplicationPermissionsModel.class);
	    System.out.println(p.declared.toString());

	    byte c[]=new byte[err.available()];
	    err.read(c,0,c.length);
	    //System.out.println(new String(c));
	}

}
