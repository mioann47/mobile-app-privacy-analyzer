package models;

import java.util.ArrayList;

public class ApplicationPermissionsModel {

	
	public ArrayList<String> declared,requiredAndUsed,requiredButNotUsed,notRequiredButUsed;
	
	
	public ApplicationPermissionsModel() {
		declared=new ArrayList<String>();
		requiredAndUsed=new ArrayList<String>();
		requiredButNotUsed =new ArrayList<String>();
		notRequiredButUsed=new ArrayList<String>();
		
	}
	
}
