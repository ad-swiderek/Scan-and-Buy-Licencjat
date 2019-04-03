package com.example.adrian.myapplication;

public class ProductClass {
    String id;
    String barcode;
    String productName;
    String price;
    String quantity;

    public ProductClass(String barcode, String productName, String price, String quantity) {
        //this.id=id;
        this.barcode = barcode;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }
}
