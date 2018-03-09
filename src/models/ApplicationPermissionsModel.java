package models;

import java.util.ArrayList;

public class ApplicationPermissionsModel {

	
	private ArrayList<String> declared,requiredAndUsed,requiredButNotUsed,notRequiredButUsed;
	


	public ApplicationPermissionsModel() {
		declared=new ArrayList<String>();
		requiredAndUsed=new ArrayList<String>();
		requiredButNotUsed =new ArrayList<String>();
		notRequiredButUsed=new ArrayList<String>();
		
	}


	public ArrayList<String> getDeclared() {
		return declared;
	}


	public void setDeclared(ArrayList<String> declared) {
		this.declared = declared;
	}


	public ArrayList<String> getRequiredAndUsed() {
		return requiredAndUsed;
	}


	public void setRequiredAndUsed(ArrayList<String> requiredAndUsed) {
		this.requiredAndUsed = requiredAndUsed;
	}


	public ArrayList<String> getRequiredButNotUsed() {
		return requiredButNotUsed;
	}


	public void setRequiredButNotUsed(ArrayList<String> requiredButNotUsed) {
		this.requiredButNotUsed = requiredButNotUsed;
	}


	public ArrayList<String> getNotRequiredButUsed() {
		return notRequiredButUsed;
	}


	public void setNotRequiredButUsed(ArrayList<String> notRequiredButUsed) {
		this.notRequiredButUsed = notRequiredButUsed;
	}
	
	
	
}
