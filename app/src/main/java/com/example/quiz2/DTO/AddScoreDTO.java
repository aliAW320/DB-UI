package com.example.quiz2.DTO;

import java.io.Serializable;

public class AddScoreDTO implements Serializable {
    int match_id;
    int score;

    public AddScoreDTO(int match_id, int score) {
        this.match_id = match_id;
        this.score = score;
    }

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "AddScoreDTO{" +
                "match_id=" + match_id +
                ", score=" + score +
                '}';
    }
}
