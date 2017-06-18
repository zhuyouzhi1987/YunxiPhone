package com.yunxi.phone.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.bean.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class ContactsManager {
    private ContentResolver contentResolver;
    private static final String TAG = "ContactsManager";

    /**
     * Use a simple string represents the long.
     */
    private static final String COLUMN_CONTACT_ID =
            ContactsContract.Data.CONTACT_ID;
    private static final String COLUMN_RAW_CONTACT_ID =
            ContactsContract.Data.RAW_CONTACT_ID;
    private static final String COLUMN_MIMETYPE =
            ContactsContract.Data.MIMETYPE;
    private static final String COLUMN_NAME =
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;
    private static final String COLUMN_NUMBER =
            ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String COLUMN_NUMBER_TYPE =
            ContactsContract.CommonDataKinds.Phone.TYPE;
    private static final String COLUMN_EMAIL =
            ContactsContract.CommonDataKinds.Email.DATA;
    private static final String COLUMN_EMAIL_TYPE =
            ContactsContract.CommonDataKinds.Email.TYPE;
    private static final String MIMETYPE_STRING_NAME =
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_PHONE =
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_EMAIL =
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_NOTE =
            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE;

    Context context;
    public ContactsManager(Context context,ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.context = context;
    }



    /**
     * Search and fill the contact information by the contact name given.
     *
     * @param contact Only the name is necessary.
     */
    public Contact searchContact(String name) {
        Log.w(TAG, "**search start**");
        Contact contact = new Contact();
        contact.setName(name);
        Log.d(TAG, "search name: " + contact.getName());
        String id = getContactID(contact.getName());
        contact.setId(id);

        if (id.equals("0")) {
            Log.d(TAG, contact.getName() + " not exist. exit.");
        } else {
            Log.d(TAG, "find id: " + id);
            //Fetch Phone Number
            Cursor cursor = contentResolver.query(
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{COLUMN_NUMBER, COLUMN_NUMBER_TYPE},
                    COLUMN_CONTACT_ID + "='" + id + "'", null, null);
//            Cursor cursor = contentResolver.query(
//                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    new String[]{COLUMN_NUMBER, COLUMN_NUMBER_TYPE},
//                    null, null, null);
            while (cursor.moveToNext()) {
                contact.setNumber(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
                contact.setNumberType(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER_TYPE)));
                Log.d(TAG, "find number: " + contact.getNumber());
                Log.d(TAG, "find numberType: " + contact.getNumberType());
            }

            cursor = contentResolver.query(
                    android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{COLUMN_EMAIL, COLUMN_EMAIL_TYPE},
                    COLUMN_CONTACT_ID + "='" + id + "'", null, null);
            while (cursor.moveToNext()) {
                contact.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                contact.setEmailType(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_TYPE)));
                Log.d(TAG, "find email: " + contact.getEmail());
                Log.d(TAG, "find emailType: " + contact.getEmailType());
            }
            cursor.close();
        }
        Log.w(TAG, "**search end**");
        return contact;
    }

    /**
     * @param contact The contact who you get the id from. The name of
     *                the contact should be set.
     * @return 0 if contact not exist in contacts list. Otherwise return
     * the id of the contact.
     */
    public String getContactID(String name) {
        String id = "0";
        Cursor cursor = contentResolver.query(
                android.provider.ContactsContract.Contacts.CONTENT_URI,
                new String[]{android.provider.ContactsContract.Contacts._ID},
                android.provider.ContactsContract.Contacts.DISPLAY_NAME +
                        "='" + name + "'", null, null);
        if (cursor!=null && cursor.moveToNext()) {
            id = cursor.getString(cursor.getColumnIndex(
                    android.provider.ContactsContract.Contacts._ID));
        }
        return id;
    }

    /**
     * You must specify the contact's ID.
     *
     * @param contact
     * @throws Exception The contact's name should not be empty.
     */
    public String addContact(Contact contact) {
        Log.w(TAG, "**add start**");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//        String id = getContactID(contact.getName());
//        if (!id.equals("0")) {
//            Log.d(TAG, "contact already exist. exit.");
//            Toast.makeText(context, "该姓名已经存在", Toast.LENGTH_SHORT).show();
//        } else if (contact.getName().trim().equals("")) {
//            Log.d(TAG, "contact name is empty. exit.");
//        } else {
        boolean add = ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                    .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_NAME)
                    .withValue(COLUMN_NAME, contact.getName())
                    .build());
            Log.d(TAG, "add name: " + contact.getName());

            if (!contact.getNumber().trim().equals("")) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                        .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_PHONE)
                        .withValue(COLUMN_NUMBER, contact.getNumber())
                        .withValue(COLUMN_NUMBER_TYPE, 2)
                        .build());
                Log.d(TAG, "add number: " + contact.getNumber());
            }
            if (!contact.getNote().trim().equals("")) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                        .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_NOTE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, contact.getNote())
                        .build());
                Log.d(TAG, "add not: " + contact.getNote());
            }
            try {
                ContentProviderResult[] contentProviderResults = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                if(contentProviderResults.length==0){
                    //添加失败
                    L.d("拒绝");
                    //发送广播
                    Intent intent = new Intent();
                    intent.setAction("action.denied");
                    //发送广播
                    context.sendBroadcast(intent);

                }
                Log.d(TAG, "add contact success.");
            } catch (Exception e) {
                Log.d(TAG, "add contact failed.");
                Log.e(TAG, e.getMessage());
            }
            Log.w(TAG, "**add end**");
//            return getContactID(contact.getName());
            return "";
//        }
    }

    /**
     * Delete contacts who's name equals contact.getName();
     *
     * @param contact
     */
    public void deleteContact(String id) {
        Log.w(TAG, "**delete start**");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

//        String id = getContactID(contact.getName());

        //delete contact
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=" + id, null)
                .build());
        //delete contact information such as phone number,email
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(COLUMN_CONTACT_ID + "=" + id, null)
                .build());
        Log.d(TAG, "delete contact: " + id);

        try {
            ContentProviderResult[] contentProviderResults = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            if(contentProviderResults.length==0){
                Intent intent = new Intent();
                intent.setAction("action.denied");
                //发送广播
                context.sendBroadcast(intent);
            }
            Log.d(TAG, "delete contact success");
        } catch (Exception e) {
            Log.d(TAG, "delete contact failed");
            Log.e(TAG, e.getMessage());
        }
        Log.w(TAG, "**delete end**");
    }

    /**
     * @param contactOld The contact wants to be updated. The name should exists.
     * @param contactNew
     */
    public void updateContact(String id, Contact contactNew) {
        Log.w(TAG, "**update start**");
//        String id = getContactID(contactOld.getName());
        if (id.equals("0")) {
//            Log.d(TAG, contactOld.getName() + " not exist.");
        } else {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            //update name
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
                            new String[]{id, MIMETYPE_STRING_NAME})
                    .withValue(COLUMN_NAME, contactNew.getName())
                    .build());
            Log.d(TAG, "update name: " + contactNew.getName());

            //update number
            if (!contactNew.getNumber().trim().equals("")) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
                                new String[]{id, MIMETYPE_STRING_PHONE})
                        .withValue(COLUMN_NUMBER, contactNew.getNumber())
                        .withValue(COLUMN_NUMBER_TYPE, 2)
                        .build());
                Log.d(TAG, "update number: " + contactNew.getNumber());
            }

            if (!contactNew.getNote().trim().equals("")) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(COLUMN_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, contactNew.getNote())
                        .build());
            }

            try {
                ContentProviderResult[] contentProviderResults = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                if(contentProviderResults.length==0){
                    Intent intent = new Intent();
                    intent.setAction("action.denied");
                    //发送广播
                    context.sendBroadcast(intent);
                }
                Log.d(TAG, "update success");
            } catch (Exception e) {
                Log.d(TAG, "update failed");
                Log.e(TAG, e.getMessage());
            }
        }
        Log.w(TAG, "**update end**");
    }
    String temp;
    public String searchContact() {
        //用户id
        List<Contact> contactList = new ArrayList<Contact>();
        String id = "";
        String number= "";
        Cursor cursorId = contentResolver.query(
                android.provider.ContactsContract.Contacts.CONTENT_URI,
                new String[]{android.provider.ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                null, null, null);
        while (cursorId!=null && cursorId.moveToNext()) {
            id = cursorId.getString(cursorId.getColumnIndex(
                    android.provider.ContactsContract.Contacts._ID));
            //名字
            String name = cursorId.getString(cursorId.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //电话
            Cursor cursorNum = contentResolver.query(
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{COLUMN_NUMBER, COLUMN_NUMBER_TYPE},
                    COLUMN_CONTACT_ID + "='" + id + "'", null, null);
            while (cursorNum !=null && cursorNum.moveToNext()) {
                number = cursorNum.getString(cursorNum.getColumnIndex(COLUMN_NUMBER));
            }
            if(number.equals(temp)){
                continue;
            }
            temp=number;
            String note = getNote(id);
            //备注
            if(cursorNum!=null){
                cursorNum.close();
            }
            Contact contact = new Contact();
            contact.setId(id + "");
            if(TextUtils.isEmpty(name)){
                contact.setName("未知联系人");
            }else{
                contact.setName(name);
            }
            contact.setNumber(number.replace(" ",""));
            contact.setNote(note+"");
            contactList.add(contact);
        }
        if(cursorId!=null){
            cursorId.close();
        }

        String jsonList = JSON.toJSONString(contactList);

        return jsonList;
    }
    public String getNote(String contactId) {
        Cursor noteCur = null;
        String note="";
        String noteWhere =
                ContactsContract.Data.CONTACT_ID + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?";

        String[] noteWhereParams = new String[]{
                contactId,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

        noteCur = this.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                noteWhere,
                noteWhereParams,
                null);
        if (noteCur !=null && noteCur.moveToFirst()) {

            note = noteCur.getString(noteCur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Note.NOTE));
        }
        if (noteCur != null) {
            noteCur.close();
        }
        return note;
    }
}
