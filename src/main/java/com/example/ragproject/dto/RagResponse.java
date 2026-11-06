package com.example.ragproject.dto;

public class RagResponse {
    private String answer;
    private String query;
    private int retrievedChunks;
    private double similarity;

    public RagResponse() {}

    public RagResponse(String answer, String query, int retrievedChunks, double similarity) {
        this.answer = answer;
        this.query = query;
        this.retrievedChunks = retrievedChunks;
        this.similarity = similarity;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public int getRetrievedChunks() { return retrievedChunks; }
    public void setRetrievedChunks(int retrievedChunks) { this.retrievedChunks = retrievedChunks; }
    public double getSimilarity() { return similarity; }
    public void setSimilarity(double similarity) { this.similarity = similarity; }
}
