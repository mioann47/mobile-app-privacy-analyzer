package models;

public class ApkModel {
	private String packageName;
	private String packageVersionName;
	private String packageVersionCode;
	private String minSDK;
	private String targetSDK;
	private String sha256;
	private String isDebuggable;
	private String isAdbBackupEnabled;
	private String appName;
	/**
	 * @param packageName
	 * @param packageVersionName
	 * @param packageVersionCode
	 * @param minSDK
	 * @param targetSDK
	 * @param sha256
	 * @param isDebuggable
	 * @param isAdbBackupEnabled
	 * @param appName
	 */
	public ApkModel(String packageName, String packageVersionName, String packageVersionCode, String minSDK,
			String targetSDK, String sha256, String isDebuggable, String isAdbBackupEnabled, String appName) {
		super();
		this.packageName = packageName;
		this.packageVersionName = packageVersionName;
		this.packageVersionCode = packageVersionCode;
		this.minSDK = minSDK;
		this.targetSDK = targetSDK;
		this.sha256 = sha256;
		this.isDebuggable = isDebuggable;
		this.isAdbBackupEnabled = isAdbBackupEnabled;
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPackageVersionName() {
		return packageVersionName;
	}
	public void setPackageVersionName(String packageVersionName) {
		this.packageVersionName = packageVersionName;
	}
	public String getPackageVersionCode() {
		return packageVersionCode;
	}
	public void setPackageVersionCode(String packageVersionCode) {
		this.packageVersionCode = packageVersionCode;
	}
	public String getMinSDK() {
		return minSDK;
	}
	public void setMinSDK(String minSDK) {
		this.minSDK = minSDK;
	}
	public String getTargetSDK() {
		return targetSDK;
	}
	public void setTargetSDK(String targetSDK) {
		this.targetSDK = targetSDK;
	}
	public String getSha256() {
		return sha256;
	}
	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}
	public String getIsDebuggable() {
		return isDebuggable;
	}
	public void setIsDebuggable(String isDebuggable) {
		this.isDebuggable = isDebuggable;
	}
	public String getIsAdbBackupEnabled() {
		return isAdbBackupEnabled;
	}
	public void setIsAdbBackupEnabled(String isAdbBackupEnabled) {
		this.isAdbBackupEnabled = isAdbBackupEnabled;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}

	

}
