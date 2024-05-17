package com.example.a71p;

public class LAF {

    private String id;
    private String Type;
    private String Name;
    private String Phone;
    private String Desc;
    private String date;
    private String location;

    public LAF(String id,String Type, String Name, String Phone, String Desc, String date, String location) {
        this.id=id;
        this.Type = Type;
        this.Name = Name;
        this.Phone = Phone;
        this.Desc = Desc;
        this.date = date;
        this.location = location;
    }

    // Getter and Setter for id
    public String getid() {
        return id;
    }

    public void setid(String Type) {
        this.id = id;
    }

    // Getter and Setter for Type
    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    // Getter and Setter for Name
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    // Getter and Setter for Phone
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    // Getter and Setter for Desc
    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    // Getter and Setter for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter and Setter for location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
