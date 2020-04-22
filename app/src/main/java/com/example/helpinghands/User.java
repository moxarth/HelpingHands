package com.example.helpinghands;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    Context context;
    SharedPreferences sharedPreferences;
    private String password;
    private String age;
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private int type;
    private String gender;
    private Long econ1;
    private String econ1name;
    private String rel1;
    private Long econ2;
    private String econ2name;
    private String rel2;
    private Long econ3;
    private String econ3name;
    private String rel3;
    private long contactnumber;
    private String lname;
    private String userid;
    private String fname;
    private String latitude;
    private String longitude;
    private int sosflag;

    public int getSosflag() {
        sosflag = sharedPreferences.getInt("sosflag",0);
        return sosflag;
    }

    public void setSosflag(int sosflag) {
        this.sosflag = sosflag;
        sharedPreferences.edit().putInt("sosflag",sosflag).commit();
    }



    public String getPassword() {
        password = sharedPreferences.getString("password","");
        return password;
    }

    public void setPassword(String latitude) {
        this.password = latitude;
        sharedPreferences.edit().putString("password",password).commit();
    }


    public String getLatitude() {
        latitude = sharedPreferences.getString("latitude","");
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
        sharedPreferences.edit().putString("latitude",latitude).commit();
    }

    public String getLongitude() {
        longitude = sharedPreferences.getString("longitude","");
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
        sharedPreferences.edit().putString("longitude",longitude).commit();
    }

    public String getAge() {
        age = sharedPreferences.getString("age","");
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        sharedPreferences.edit().putString("age",age).commit();
    }

    public String getEmail() {
        email = sharedPreferences.getString("email","");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        sharedPreferences.edit().putString("email",email).commit();
    }

    public String getAddress() {
        address = sharedPreferences.getString("address","");
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        sharedPreferences.edit().putString("address",address).commit();
    }

    public String getCity() {
        city = sharedPreferences.getString("city","");
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        sharedPreferences.edit().putString("city",city).commit();
    }

    public String getState() {
        state = sharedPreferences.getString("state","");
        return state;
    }

    public void setState(String state) {
        this.state = state;
        sharedPreferences.edit().putString("state",state).commit();
    }

    public String getCountry() {
        country = sharedPreferences.getString("country","");
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        sharedPreferences.edit().putString("country",country).commit();
    }

    public int getType() {
        type = sharedPreferences.getInt("type",0);
        return type;
    }

    public void setType(int type) {
        this.type = type;
        sharedPreferences.edit().putInt("type",type).commit();
    }

    public String getGender() {
        gender = sharedPreferences.getString("gender","");
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        sharedPreferences.edit().putString("gender",gender).commit();
    }

    public Long getEcon1() {
        econ1 = sharedPreferences.getLong("econ1",0);
        return econ1;
    }

    public void setEcon1(Long econ1) {
        this.econ1 = econ1;
        sharedPreferences.edit().putLong("econ1",econ1).commit();
    }

    public String getEcon1name() {
        econ1name = sharedPreferences.getString("econ1name","");
        return econ1name;
    }

    public void setEcon1name(String econ1name) {
        this.econ1name = econ1name;
        sharedPreferences.edit().putString("econ1name",econ1name).commit();
    }

    public String getRel1() {
        rel1 = sharedPreferences.getString("rel1","Parent");
        return rel1;
    }

    public void setRel1(String rel1) {
        this.rel1 = rel1;
        sharedPreferences.edit().putString("rel1",rel1).commit();
    }

    public Long getEcon2() {
        econ2 = sharedPreferences.getLong("econ2",0);
        return econ2;
    }

    public void setEcon2(Long econ2) {
        this.econ2 = econ2;
        sharedPreferences.edit().putLong("econ2",econ2).commit();
    }

    public String getEcon2name() {
        econ2name = sharedPreferences.getString("econ2name","");
        return econ2name;
    }

    public void setEcon2name(String econ2name) {
        this.econ2name = econ2name;
        sharedPreferences.edit().putString("econ2name",econ2name).commit();
    }

    public String getRel2() {
        rel2 = sharedPreferences.getString("rel2","Parent");
        return rel2;
    }

    public void setRel2(String rel2) {
        this.rel2 = rel2;
        sharedPreferences.edit().putString("rel2",rel2).commit();
    }

    public Long getEcon3() {
        econ3 = sharedPreferences.getLong("econ3",0);
        return econ3;
    }

    public void setEcon3(Long econ3) {
        this.econ3 = econ3;
        sharedPreferences.edit().putLong("econ3",econ3).commit();
    }

    public String getEcon3name() {
        econ3name = sharedPreferences.getString("econ3name","");
        return econ3name;
    }

    public void setEcon3name(String econ3name) {
        this.econ3name = econ3name;
        sharedPreferences.edit().putString("econ3name",econ3name).commit();
    }

    public String getRel3() {
        rel3 = sharedPreferences.getString("rel3","Parent");
        return rel3;
    }

    public void setRel3(String rel3) {
        this.rel3 = rel3;
        sharedPreferences.edit().putString("rel3",rel3).commit();
    }

    public  void removeUser(){
        sharedPreferences.edit().clear().commit();
    }

    public long getContactnumber() {
        contactnumber = sharedPreferences.getLong("contact",0);
        return contactnumber;
    }

    public void setContactnumber(Long contactnumber) {
        this.contactnumber = contactnumber;
        sharedPreferences.edit().putLong("contact",contactnumber).commit();
    }

    public String getUserid() {
        userid = sharedPreferences.getString("userid","");
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
        sharedPreferences.edit().putString("userid",userid).commit();
    }

    public String getFName() {
        fname = sharedPreferences.getString("userfname","");
        return fname;
    }

    public void setFName(String fname) {
        this.fname = fname;
        sharedPreferences.edit().putString("userfname",fname).commit();
    }

    public String getLName() {
        lname = sharedPreferences.getString("userlname","");
        return lname;
    }

    public void setLName(String lname) {
        this.lname = lname;
        sharedPreferences.edit().putString("userlname",lname).commit();
    }

    public User(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userinfo",context.MODE_PRIVATE);
    }
}
