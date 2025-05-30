package com.example.quiz2.DTO;

public class MatchDTO {
    private int match_id;
    private String player1_username;
    private String player2_username;

    public MatchDTO(int match_id, String player1_username, String player2_username) {
        this.match_id = match_id;
        this.player1_username = player1_username;
        this.player2_username = player2_username;
    }

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public String getPlayer1_username() {
        return player1_username;
    }

    public void setPlayer1_username(String player1_username) {
        this.player1_username = player1_username;
    }

    public String getPlayer2_username() {
        return player2_username;
    }

    public void setPlayer2_username(String player2_username) {
        this.player2_username = player2_username;
    }
}