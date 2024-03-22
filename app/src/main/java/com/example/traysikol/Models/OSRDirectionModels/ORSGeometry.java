package com.example.traysikol.Models.OSRDirectionModels;

public class ORSGeometry {
    public double[][] coordinates;
    public String type;

    public  ORSGeometry(){}
    public ORSGeometry(double[][] coordinates, String type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][] coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}