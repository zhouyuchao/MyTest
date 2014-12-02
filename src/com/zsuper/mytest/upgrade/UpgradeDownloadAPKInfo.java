package com.zsuper.mytest.upgrade;

public class UpgradeDownloadAPKInfo {
    private String downloadUrl;
    private String isForceUpgrade;
    private boolean isServerCheckIMei;
    private String md5;
    private String packageName;
    private String smartUpgradeVersion;
    private String upgradeIntroduce;
    private int versionCode;
    private String versionName;
    private long fileSize;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getIsForceUpgrade() {
        return isForceUpgrade;
    }

    public void setIsForceUpgrade(String isForceUpgrade) {
        this.isForceUpgrade = isForceUpgrade;
    }

    public boolean isServerCheckIMei() {
        return isServerCheckIMei;
    }

    public void setServerCheckIMei(boolean isServerCheckIMei) {
        this.isServerCheckIMei = isServerCheckIMei;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSmartUpgradeVersion() {
        return smartUpgradeVersion;
    }

    public void setSmartUpgradeVersion(String smartUpgradeVersion) {
        this.smartUpgradeVersion = smartUpgradeVersion;
    }

    public String getUpgradeIntroduce() {
        return upgradeIntroduce;
    }

    public void setUpgradeIntroduce(String upgradeIntroduce) {
        this.upgradeIntroduce = upgradeIntroduce;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

}
