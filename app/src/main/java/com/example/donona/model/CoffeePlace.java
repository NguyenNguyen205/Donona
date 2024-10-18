package com.example.donona.model;

public class CoffeePlace {
    private String address;
    private String city;
    private String display;
    private String district;
    private String endtime;
    private String hs_num;
    private String image;
    private double lat;
    private double lng;
    private String name;
    private String priceRange;
    private String startTime;
    private String street;
    private String thumbnail;
    private String ward;
    private boolean wifi;
    private String ref_id;
    private boolean isBookMark;

    // Constructor không đối số (cần thiết cho Firebase)
    public CoffeePlace() {
    }

    // Constructor đầy đủ đối số
    public CoffeePlace(String address, String city, String display, String district, String endtime, String hs_num,
                      String image, double lat, double lng, String name, String priceRange, String startTime, String street,
                      String thumbnail, String ward, boolean wifi, boolean isBookMark) {
        this.address = address;
        this.city = city;
        this.display = display;
        this.district = district;
        this.endtime = endtime;
        this.hs_num = hs_num;
        this.image = image;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.priceRange = priceRange;
        this.startTime = startTime;
        this.street = street;
        this.thumbnail = thumbnail;
        this.ward = ward;
        this.wifi = wifi;
        this.isBookMark = isBookMark;
    }

    // Getters và Setters cho tất cả các thuộc tính
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getHs_num() {
        return hs_num;
    }

    public void setHs_num(String hs_num) {
        this.hs_num = hs_num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    private void setRef_id(String refId) {
        this.ref_id = refId;
    }

    public String getRef_id() { return this.ref_id; }

    public boolean isBookMark() {
        return isBookMark;
    }

    public void setBookMark(boolean isBookMark) {
        this.isBookMark = isBookMark;
    }

}

