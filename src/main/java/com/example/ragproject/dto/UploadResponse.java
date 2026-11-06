package com.example.ragproject.dto;

public class UploadResponse {
    private String id;
    private String filename;
    private long size;
    private int chunksCreated;
    private String message;

    public UploadResponse() {}

    public UploadResponse(String id, String filename, long size, int chunksCreated, String message) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.chunksCreated = chunksCreated;
        this.message = message;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public int getChunksCreated() { return chunksCreated; }
    public void setChunksCreated(int chunksCreated) { this.chunksCreated = chunksCreated; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
