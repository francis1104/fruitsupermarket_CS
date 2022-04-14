package com.itheima.domain;

import java.io.Serializable;

public class Fruit implements Serializable {
    private String id;
    private String name;
    private int price;
    private String unit;
    private int number;

    @Override
    public String toString() {
        return id+","+name+","+price+","+unit+","+number;
    }

    public Fruit() {
    }

    public Fruit(String id, String name, int price, String unit, int number) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
