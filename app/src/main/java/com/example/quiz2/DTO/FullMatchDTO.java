package com.example.quiz2.DTO;

public class FullMatchDTO {
    private int match_id;
    private String opponent;
    private int user_score;
    private int opponent_score;

    public FullMatchDTO(int match_id, String opponent, int user_score, int opponent_score) {
        this.match_id = match_id;
        this.opponent = opponent;
        this.user_score = user_score;
        this.opponent_score = opponent_score;
    }

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public int getUser_score() {
        return user_score;
    }

    public void setUser_score(int user_score) {
        this.user_score = user_score;
    }

    public int getOpponent_score() {
        return opponent_score;
    }

    public void setOpponent_score(int opponent_score) {
        this.opponent_score = opponent_score;
    }
}