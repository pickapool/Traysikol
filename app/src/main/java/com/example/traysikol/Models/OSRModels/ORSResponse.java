package com.example.traysikol.Models.OSRModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ORSResponse {
    public String type;
    public ORSMetadata metadata;
    public double[] bbox;
    public List<ORSFeature> features;
}
