package com.example.traysikol.Models;

public class FareModel {
    public double studentFare;
    public double regularFare;
    public double studentKM;
    public double regularKM;
    public FareModel(){}
    public FareModel(double studentFare, double regularFare, double studentKM, double regularKM) {
        this.studentFare = studentFare;
        this.regularFare = regularFare;
        this.studentKM = studentKM;
        this.regularKM = regularKM;
    }

    public double getStudentFare() {
        return studentFare;
    }

    public void setStudentFare(double studentFare) {
        this.studentFare = studentFare;
    }

    public double getRegularFare() {
        return regularFare;
    }

    public void setRegularFare(double regularFare) {
        this.regularFare = regularFare;
    }

    public double getStudentKM() {
        return studentKM;
    }

    public void setStudentKM(double studentKM) {
        this.studentKM = studentKM;
    }

    public double getRegularKM() {
        return regularKM;
    }

    public void setRegularKM(double regularKM) {
        this.regularKM = regularKM;
    }
}
