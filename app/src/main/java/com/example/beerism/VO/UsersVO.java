package com.example.beerism.VO;

public class UsersVO {
    private String name;
    private String Recommend;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String email;
    private String password;

    public String getRecommend() {
        return Recommend;
    }

    public void setRecommend(String recommend) {
        Recommend = recommend;
    }


}
