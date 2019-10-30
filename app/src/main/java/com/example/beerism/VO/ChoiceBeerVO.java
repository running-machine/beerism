package com.example.beerism.VO;

public class ChoiceBeerVO {
    public final String description;
    public final String amount;

    public ChoiceBeerVO(String amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    @Override
    public String toString() {
        return "DonationOption{" +
                "description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
