package com.example.igifted;

public class DataClass {
    private String dataTitle;
    private String dataDesc;
    private String dataPrice;
    private int quantity; // Changed to int
    private String dataImage;
    private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataPrice() {
        return dataPrice;
    }
    public int getQuantity() {
        return quantity;
    } // Getter for quantity
    public void setQuantity(int quantity) { // Setter for quantity
        this.quantity = quantity;
    }
    public String getDataImage() {
        return dataImage;
    }
    public DataClass(String dataTitle, String dataDesc, String dataPrice, String dataImage, int quantity) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataPrice = dataPrice;
        this.dataImage = dataImage;
        this.quantity = quantity;
    }
    public DataClass(){
    }
}
