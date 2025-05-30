package com.example.quiz2.DTO;

import java.util.ArrayList;

public class MainPageDTO {
    private String userName;
    private ArrayList<MatchDTO> matches ;

    public MainPageDTO(String userName, ArrayList<MatchDTO> matches) {
        this.userName = userName;
        this.matches = matches;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<MatchDTO> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<MatchDTO> matches) {
        this.matches = matches;
    }
}
