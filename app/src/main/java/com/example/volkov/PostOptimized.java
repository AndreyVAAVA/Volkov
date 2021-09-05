package com.example.volkov;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostOptimized {
    public String gifLink;
    public String description;
}
