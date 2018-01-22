package jsoup.html.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupExtractor {

	public StringBuilder getBasicPermissions() throws IOException {

		Document doc = Jsoup.connect("https://developer.android.com/reference/android/Manifest.permission.html").get();
		

		Elements newsHeadlines = doc.select("div.api");

		StringBuilder sb = new StringBuilder();

		for (Element headline : newsHeadlines) {

			Elements t1 = headline.getElementsByClass("api-name");
			String permissionName = t1.get(0).text();
			Elements t2 = headline.select("p");
			String permissionDesc = t2.get(0).text();
			if (permissionDesc.contains("public static final class"))
				continue;
			String protectionLevel = "";
			String permissionValue = "";
			int check = 0;
			for (int i = 1; i < t2.size(); i++) {

				if (t2.get(i).text().contains("Protection level: ")
						|| t2.get(i).text().contains("Not for use by third-party applications.")) {
					protectionLevel = t2.get(i).text();

				} else if (t2.get(i).text().contains("Constant Value: ")) {
					permissionValue = t2.get(i).text();
					check++;
				}

			}
			if (check == 0)
				continue;

			sb.append(permissionName);
			sb.append(";");
			sb.append(permissionDesc);
			sb.append(";");
			protectionLevel = protectionLevel.replace("Protection level: ", "");
			sb.append(protectionLevel);
			sb.append(";");

			permissionValue = permissionValue.replace("Constant Value: ", "");
			permissionValue = permissionValue.replace("\"", "");
			sb.append(permissionValue);
			sb.append("\n");

		}

		return sb;
	}

	public StringBuilder getThridPartyPermissions(StringBuilder sb) throws FileNotFoundException {

		Scanner scanner = new Scanner(new File("all_android_permissions.md.txt"));

		while (scanner.hasNext()) {

			String line = scanner.nextLine();
			String constanValue = line.split("permission:")[1].split("`")[0];

			line = scanner.nextLine();
			line = scanner.nextLine();
			line = scanner.nextLine();
			String permissionDesc = line.split("Description\\*\\* ")[1];
			if (permissionDesc.equals("null  "))
				permissionDesc = "";
			else {
				int permissionDescsize = permissionDesc.length();
				permissionDesc = permissionDesc.substring(0, permissionDescsize - 2);

			}
			line = scanner.nextLine();
			String protectionLevel = line.split("Protection Level\\*\\* ")[1];

			if (protectionLevel.equals("null"))
				protectionLevel = "";
			String tokens[] = constanValue.split("\\.");
			String permName = tokens[tokens.length - 1];

			sb.append(permName);
			sb.append(";");
			sb.append(permissionDesc);
			sb.append(";");

			sb.append(protectionLevel);
			sb.append(";");

			sb.append(constanValue);
			sb.append("\n");
		}
		scanner.close();

		return sb;
	}

	public static void main(String[] args) throws IOException {

		JsoupExtractor je = new JsoupExtractor();
		StringBuilder x = new StringBuilder();
		x = je.getBasicPermissions();
		x = je.getThridPartyPermissions(x);


		String perms[] = x.toString().split("\n");
		x = new StringBuilder();
		ArrayList<String> permlist = new ArrayList<String>();

		permlist.addAll(Arrays.asList(perms));
		Collections.sort(permlist);
		for (String x1 : permlist) {
			x.append(x1);
			x.append("\n");
		}
		
		 PrintWriter pw = new PrintWriter(new File("permissionsDesc.csv"));
		 pw.write(x.toString());
		 pw.close();
		 System.out.println("Done");
	}

}
