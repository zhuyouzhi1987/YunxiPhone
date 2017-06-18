package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/5.
 */
public class Contact {
    private String email;
    private String emailType;
    private String id;
    private String name;
    private String number;
    private String numberType;
    private String note;
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }



    public Contact(){
    }

    public Contact(Contact contact){
        this.name = contact.getName();
        this.number = contact.getNumber();
        this.numberType = contact.getNumberType();
        this.email = contact.getEmail();
        this.emailType = contact.getEmailType();
        this.note = contact.getNote();
    }

    public String getEmail() {
        return email;
    }
    public String getEmailType() {
        return emailType;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getNumber() {
        return number;
    }
    public String getNumberType() {
        return numberType;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }
}
