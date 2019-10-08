package com.example.cyclovillev4;

public class Product {

    private String product_id;
    private String product_name;
    private String product_price;
    private String quantity;
    private String category;
    private String description;
    private String image;

    private Product(String product_id, String product_name, String product_price, String quantity, String description, String category, String image) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
        this.image = image;

    }

    public String getId() {

        return product_id;
    }

    public void setId(String product_id) {

        this.product_id = product_id;
    }

    public String getName() {

        return product_name;
    }

    public void setName(String product_name) {

        this.product_name = product_name;
    }

    String getPrice() {

        return product_price;
    }

    public void setPrice(String product_price) {

        this.product_price = product_price;
    }

    private String getQuantity() {

        return quantity;
    }

    public void setQuantity(String quantity) {

        this.quantity = quantity;
    }

    private String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    private String getCategory() {

        return category;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    private String getImage() {

        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }
}
