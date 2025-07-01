package com.example.quiz2.DTO;

import java.io.Serializable;

public class CategoryEntryDTO implements Serializable {
    private int category_id;
    private int match_id;

    public CategoryEntryDTO(int category_id, int match_id)  {
        this.category_id = category_id;
        this.match_id = match_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }
}
