package com.example.cyclovillev4;

public class Product {

    public String product_id;
    public String product_name;
    public String product_price;
    public String quantity;
    public String category;
    public String description;
    public String image;

    public Product(String product_id, String product_name, String product_price, String quantity, String description, String category, String image) {
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

    public String getPrice() {

        return product_price;
    }

    public void setPrice(String product_price) {

        this.product_price = product_price;
    }

    public String getQuantity() {

        return quantity;
    }

    public void setQuantity(String quantity) {

        this.quantity = quantity;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    public String getImage() {

        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }
}
