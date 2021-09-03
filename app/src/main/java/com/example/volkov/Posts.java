package com.example.volkov;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Posts {
    @SerializedName("result")
    private ArrayList<Post> result;
}
