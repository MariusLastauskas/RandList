package com.example.mainpage;

public class WishlistData {
    long id;
    String name;
    String description;
    String image;
    long fk_parent;
    FrequencyData frequency;


    public WishlistData(long id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.fk_parent = -1;
        this.frequency = null;
    }

    public WishlistData(long id, String name, String description, String image, long fk_parent, FrequencyData frequency) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.fk_parent = fk_parent;
        this.frequency = frequency;
    }

    public long getId() {
        return  id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public long getFk_parent() {
        return fk_parent;
    }

    public FrequencyData getFrequency() {
        return frequency;
    }
}
