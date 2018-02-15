package models;

public class ApkModel {
	String packageName;
	String packageVersionName;
	int packageVersionCode;
	public int minSDK;
	int targetSDK;
	String sha256;
	public boolean isDebuggable;
	boolean isAdbBackupEnabled;
	String appName;

	public ApkModel(String packagename, String packageversionname, int packageversioncode, int minsdk, int targetsdk,
			String sha256, boolean is_debuggable, boolean is_adb_backup_enabled, String appname) {
		this.packageName=packagename;
		this.packageVersionName=packageversionname;
		this.packageVersionCode=packageversioncode;
		this.minSDK=minsdk;
		this.targetSDK=targetsdk;
		this.sha256=sha256;
		this.isDebuggable=is_debuggable;
		this.isAdbBackupEnabled=is_adb_backup_enabled;
		this.appName=appname;
	}
}
