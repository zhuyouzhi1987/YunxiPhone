package com.yunxi.phone.bean;

public class SortModel {

	private String name;   //��ʾ������
	private String sortLetters;  //��ʾ����ƴ��������ĸ
    private String phone;  //

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	private String tag;//1 标题  0内容
	public String getId() {
		return id;
	}

	public String getNote() {
		return note;
	}

	private String id;  //

	public void setNote(String note) {
		this.note = note;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String note;  //

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
