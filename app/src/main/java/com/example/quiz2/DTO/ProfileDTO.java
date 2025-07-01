package com.example.quiz2.DTO;

public class ProfileDTO {
    private String userName;
    private int totalMatches;
    private int winMatches;
    private double avrage;
    private int totalRank;
    private int monthlyRank;
    private int weeklyRank;
    public ProfileDTO() {}

    public ProfileDTO(String userName, int totalMatches, int winMatches, double avrage, int totalRank, int monthlyRank, int weeklyRank) {
        this.userName = userName;
        this.totalMatches = totalMatches;
        this.winMatches = winMatches;
        this.avrage = avrage;
        this.totalRank = totalRank;
        this.monthlyRank = monthlyRank;
        this.weeklyRank = weeklyRank;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public int getWinMatches() {
        return winMatches;
    }

    public void setWinMatches(int winMatches) {
        this.winMatches = winMatches;
    }

    public double getAvrage() {
        return avrage;
    }

    public void setAvrage(double avrage) {
        this.avrage = avrage;
    }

    public int getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(int totalRank) {
        this.totalRank = totalRank;
    }

    public int getMonthlyRank() {
        return monthlyRank;
    }

    public void setMonthlyRank(int monthlyRank) {
        this.monthlyRank = monthlyRank;
    }

    public int getWeeklyRank() {
        return weeklyRank;
    }

    public void setWeeklyRank(int weeklyRank) {
        this.weeklyRank = weeklyRank;
    }
}
