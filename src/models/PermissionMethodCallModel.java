package models;

public class PermissionMethodCallModel {
	String permissionName;
	String callerFunction;
	String permissionFunction;

	PermissionMethodCallModel(String callerFunction, String permissionFunction, String permissionName) {
		this.permissionName = permissionName;
		this.callerFunction = callerFunction;
		this.permissionFunction = permissionFunction;

	}

	public void print() {
		System.out.println(permissionName);
		System.out.println(callerFunction);
		System.out.println(permissionFunction);
	}

	@Override
	public boolean equals(Object o) {
		PermissionMethodCallModel x = (PermissionMethodCallModel) o;
		if (this.permissionName.equals(x.permissionName) && this.callerFunction.equals(x.callerFunction)
				&& this.permissionFunction.equals(x.permissionFunction))
			return true;
		return false;
	}
	@Override
	public String toString() {
		return permissionName+"\n"+callerFunction+"\n"+permissionFunction+"\n\n";
		
	}
}
