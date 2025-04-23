package com;

import javafx.scene.image.Image;

public class Product {
    private final int id;
    private final String name;
    private final Image image;
    private final String description; // New field

    // Original constructor - maintains backward compatibility
    public Product(int id, String name, Image image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = ""; // Default empty description
    }

    // New constructor with description
    public Product(int id, String name, Image image, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
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

    // Add new getter
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}