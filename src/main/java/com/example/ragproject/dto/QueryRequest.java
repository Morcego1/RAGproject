package com.example.ragproject.dto;

public class QueryRequest {
    private String query;
    private int topK = 5;
    private double similarityThreshold = 0.5;

    public QueryRequest() {}

    public QueryRequest(String query, int topK, double similarityThreshold) {
        this.query = query;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    public double getSimilarityThreshold() { return similarityThreshold; }
    public void setSimilarityThreshold(double similarityThreshold) { this.similarityThreshold = similarityThreshold; }
}