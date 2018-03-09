package functionalities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import models.ApkModel;
import models.ApplicationPermissionsModel;
import models.LibraryModel;
import models.Paths;
import models.PermissionMethodCallModel;

public class APKAnalyzer {

	public LibraryModel[] getLibrariesPermissions(String apkPath) throws IOException {
		String run = "python " + Paths.liteRadarPath + " -f " + apkPath;
		Process p = Runtime.getRuntime().exec(run);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder sb = new StringBuilder();

		String line = null;

		while ((line = in.readLine()) != null) {
			// System.out.println(line);
			sb.append(line + "\n");
		}

		String x = sb.toString();
		// System.out.println(x);

		LibraryModel[] libModels = null;
		Gson g = new Gson();
		libModels = g.fromJson(x, LibraryModel[].class);

		// System.out.println("PRINT "+libModels[0].Library);
		return libModels;
	}

	public ApplicationPermissionsModel getAPKPermissions(String apkPath) throws IOException, InterruptedException {

		String run = "java -jar " + Paths.permissionCheckerPath + " " + apkPath;
		Process proc = Runtime.getRuntime().exec(run);
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

	public ApkModel getApkInformation(String apkPath) throws IOException {
		String run = "python " + Paths.pythonScript + " -m analyze -f " + apkPath;
		Process p = Runtime.getRuntime().exec(run);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder sb = new StringBuilder();

		BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line = null;
		while ((line = err.readLine()) != null) {
			// System.out.println(line);
			// sb.append(line + "\n");
		}
		line = null;

		while ((line = in.readLine()) != null) {
			// System.out.println(line);
			sb.append(line + "\n");
		}

		String x = sb.toString();
		System.out.println(x);
		Gson g = new Gson();
		ApkModel apk = g.fromJson(x, ApkModel.class);
		return apk;
	}

	public ArrayList<PermissionMethodCallModel> getCalls(String apkPath, ArrayList<String> permissionList)
			throws IOException {

		String pList = "";
		for (int i = 0; i < permissionList.size(); i++) {
			pList = pList + permissionList.get(i) + ",";
		}

		String run = "python " + Paths.pythonScript + " -m call -f " + apkPath + " -p " + pList;
		Process p = Runtime.getRuntime().exec(run);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder sb = new StringBuilder();

		String line = null;

		while ((line = in.readLine()) != null) {
			// System.out.println(line);
			sb.append(line + "\n");
		}

		String x = sb.toString();
		// System.out.println(x);
		Gson g = new Gson();
		ArrayList<PermissionMethodCallModel> calls = g.fromJson(x,
				new TypeToken<ArrayList<PermissionMethodCallModel>>() {
				}.getType());

		return uniqueList(calls);

	}

	private ArrayList<PermissionMethodCallModel> uniqueList(ArrayList<PermissionMethodCallModel> list) {
		ArrayList<PermissionMethodCallModel> x = new ArrayList<PermissionMethodCallModel>();
		for (PermissionMethodCallModel pmc : list) {
			if (!x.contains(pmc)) {
				x.add(pmc);
			}

		}
		return x;

	}

	public static void main(String[] args) throws Exception {

		// ArrayList<String> permissionList= new ArrayList<String>();
		// permissionList.add("android.permission.CHANGE_WIFI_STATE");
		// permissionList.add("android.permission.ACCESS_WIFI_STATE");
		// ArrayList<PermissionMethodCallModel> x=new
		// APKAnalyzer().getCalls("apks/app2.apk", permissionList);

		// System.out.println(x.toString());

		new APKAnalyzer().getApkInformation("apks/app2.apk");

	}
}