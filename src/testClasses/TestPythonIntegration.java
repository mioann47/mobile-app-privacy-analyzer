package testClasses;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import com.google.gson.Gson;

import models.LibraryModel;

public class TestPythonIntegration {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 PythonInterpreter interpreter = new PythonInterpreter();
		    interpreter.execfile("mypythonscripts/literadar.py");
		    PyObject str = interpreter.eval("repr(myClass.run(myClass(),'mypythonscripts/app2.apk'))");
		    System.out.println(str.toString());
		    interpreter.close();
		    LibraryModel[] libModels;
		    Gson g= new Gson();
		    libModels = g.fromJson(str.toString(), LibraryModel[].class);
		    System.out.println("PRINT "+libModels[0].Library);
	}

}
