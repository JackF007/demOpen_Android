package com.gaicomo.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PreferencesManager {


    public String search                  = "search";
    public String id                      = "id";
    public String name                    = "name";
    public String email                   = "email";
    public String mobile                  = "mobile";
    public String user_type               = "user_type";
    public String user_name               = "user_name";
    public String profile_image           = "profile_image";
    public String Dob                     = "dob";
    public String array                   = "array";


    private final String PREF_NAME = "SystemPreference";
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public PreferencesManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearPreference() {
        editor.clear();
        editor.commit();
    }



    public void clearQuestion(){
        editor.remove(array);
        editor.commit();
    }


    public String getId() {
        return pref.getString(id,"");
    }

    public void setId(String id) {
        editor.putString(this.id,id);
        editor.commit();
    }

    public String getName() {
        return pref.getString(name,"");
    }

    public void setName(String name) {
        editor.putString(this.name,name);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(email,"");
    }

    public void setEmail(String email) {
        editor.putString(this.email,email);
        editor.commit();
    }

    public String getMobile() {
        return pref.getString(mobile,"");
    }

    public void setMobile(String mobile) {
        editor.putString(this.mobile,mobile);
        editor.commit();
    }

    public String getUser_type() {
        return pref.getString(user_type,"");
    }

    public void setUser_type(String user_type) {
        editor.putString(this.user_type,user_type);
        editor.commit();
    }


    public String getUser_name() {
        return pref.getString(user_name,"");
    }

    public void setUser_name(String user_type) {
        editor.putString(this.user_name,user_type);
        editor.commit();
    }
    public String getProfile_image() {
        return pref.getString(profile_image,"");
    }

    public void setProfile_image(String profile_image) {
        editor.putString(this.profile_image,profile_image);
        editor.commit();
    }



    public String getSearch() {
        return pref.getString(search,"");
    }

    public void setSearch(String search) {
        editor.putString(this.search,search);
        editor.commit();
    }


    public int getMembership() {
        return pref.getInt("package",0);
    }

    public void setMembership(int membership) {
        editor.putInt("package",membership);
        editor.commit();
    }



    public String getArray() {
        return  pref.getString(this.array,"");
    }

    public void setArray(String array) {
        editor.putString(this.array,array);
        editor.commit();
    }




    public String getDob() {
        return  pref.getString(this.Dob,"");

    }

    public void setDob(String dob) {
        editor.putString(this.Dob,dob);
        editor.commit();
    }








}
