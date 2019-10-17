package com.example.beerism.VO;

public class beerVO {
    private String beer_image;
    private String name_ko;
    private String name_en;
    private String alc;
    private String category;
    private String country;
    private String homepage;
    private String ad;


    public beerVO(String beer_image, String name_ko, String name_en, String alc, String category, String country, String homepage, String ad) {
        this.beer_image = beer_image;
        this.name_ko = name_ko;
        this.name_en = name_en;
        this.alc = alc;
        this.category = category;
        this.country = country;
        this.homepage = homepage;
        this.ad = ad;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getAlc() {
        return alc;
    }

    public void setAlc(String alc) {
        this.alc = alc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_ko() {
        return name_ko;
    }

    public void setName_ko(String name_ko) {
        this.name_ko = name_ko;
    }

    public String getBeer_image() {
        return beer_image;
    }

    public void setBeer_image(String beer_image) {
        this.beer_image = beer_image;
    }
}
