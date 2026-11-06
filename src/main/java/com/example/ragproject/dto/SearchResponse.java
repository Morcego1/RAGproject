package com.example.ragproject.dto;

public class SearchResponse {
    private String chunkId;
    private String documentId;
    private String filename;
    private String content;
    private double similarity;
    private int chunkIndex;

    public SearchResponse() {}

    public SearchResponse(String chunkId, String documentId, String filename, String content, double similarity, int chunkIndex) {
        this.chunkId = chunkId;
        this.documentId = documentId;
        this.filename = filename;
        this.content = content;
        this.similarity = similarity;
        this.chunkIndex = chunkIndex;
    }

    public String getChunkId() { return chunkId; }
    public void setChunkId(String chunkId) { this.chunkId = chunkId; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public double getSimilarity() { return similarity; }
    public void setSimilarity(double similarity) { this.similarity = similarity; }
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
}
