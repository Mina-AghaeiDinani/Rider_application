package com.example.deliveryman;

public class RiderProfile {
    //Property name must be the same as what we defined in real time database
    private String name, phone, email ,shortdescription,imageUrl;
    private float rating;
    private int numCustomerOpinion;
    public RiderProfile() {
        //Constructor , it is needed
    }

    public RiderProfile(String name, String phone, String email, String shortdescription,
                        String imageUrl,int numCustomerOpinion, float rating) {
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.rating = 0;
        this.numCustomerOpinion = 0;
        this.name = name;
        this.email = email;
        if (shortdescription.trim().equals("")) {
            this.shortdescription = "Information is not provided";
        } else this.shortdescription = shortdescription;

    }

    public float getRating() {
        return rating;
    }

    public int getNumCustomerOpinion() {
        return numCustomerOpinion;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setNumCustomerOpinion(int numCustomerOpinion) {
        this.numCustomerOpinion = numCustomerOpinion;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone =phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortdescription() {
        return shortdescription;
    }
    public void setShortdescription(String shortdescription) {
        this.shortdescription = shortdescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
