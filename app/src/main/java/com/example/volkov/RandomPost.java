package com.example.volkov;

import lombok.Data;

@Data
public class RandomPost{
    private int id;
    private String description;
    private int votes;
    private String author;
    private String date;
    private String gifURL;
    private int gifSize;
    private String previewURL;
    private String videoURL;
    private String videoPath;
    private long videoSize;
    private String type;
    private String width;
    private String height;
    private int commentsCount;
    private long fileSize;
    private boolean canVote;
}
