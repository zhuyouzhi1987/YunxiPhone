package com.yunxi.phone.eventtype;

public class ShareAdsType {

	private String url;

	private int adsId;

	private int adsType;

	private String adsContent;

	public ShareAdsType(int id,String url,int type,String content) {
		this.url = url;
		this.adsId=id;
		this.adsType=type;
		this.adsContent=content;
	}

	public String getUrl() {
		return url;
	}

	public int getAdsId() {
		return adsId;
	}

	public int getAdsType() {
		return adsType;
	}

	public String getAdsContent() {
		return adsContent;
	}
}
