package com.khronos.model;

public class Project {

    private final int id;
    private final String name;
    private final String color;

    public Project(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return name;
    }
}
