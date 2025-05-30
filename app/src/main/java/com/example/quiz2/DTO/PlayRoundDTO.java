package com.example.quiz2.DTO;

import java.util.ArrayList;

public class PlayRoundDTO {
    private String Massage;
    private ArrayList<?> objects;

    public PlayRoundDTO(String massage, ArrayList<?> objects) {
        Massage = massage;
        this.objects = objects;
    }

    public String getMassage() {
        return Massage;
    }

    public void setMassage(String massage) {
        Massage = massage;
    }

    public ArrayList<?> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<?> objects) {
        this.objects = objects;
    }
}


