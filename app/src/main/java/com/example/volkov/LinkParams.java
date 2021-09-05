package com.example.volkov;

public enum LinkParams {
    HOT("hot"), LATEST("latest"), TOP("top"), RANDOM("random");
    private String type;
    LinkParams (String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
