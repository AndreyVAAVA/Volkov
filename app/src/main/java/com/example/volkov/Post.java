package com.example.volkov;

import lombok.Data;

@Data
public class Post {
    int id;
    String description;
    int votes;
    String author;
    String date;
    String gifURL;
    int gifSize;
    String previewURL;
    String videoURL;
    String videoPath;
    long videoSize;
    String type;
    String width;
    String height;
    int commentsCount;
    long fileSize;
    boolean canVote;
}
