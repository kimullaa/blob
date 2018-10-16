package com.example.blob;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class File {
    private String id;
    private String name;
    private InputStream in;
}
