package com;

import javafx.scene.image.Image;

public class Product {
    private final int id;
    private final String name;
    private final Image image;

    public Product(int id, String name, Image image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return name;
    }
}