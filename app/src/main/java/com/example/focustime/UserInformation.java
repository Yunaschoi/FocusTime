package com.example.focustime;

public class UserInformation {
    private String name;
    private String phoneNumber;
    private String address;
    private String photoUrl;

    public UserInformation() {
    }

    public UserInformation(String name, String phoneNumber, String address, String photoUrl) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.photoUrl = photoUrl;

    }
    public UserInformation(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {

        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {

        return this.address;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhotoUrl() {

        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl)
    {
        this.photoUrl = photoUrl;
    }

}