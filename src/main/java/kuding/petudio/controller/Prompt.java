package kuding.petudio.controller;

import kuding.petudio.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
public class Prompt{
    public Prompt(int section, String content) {
        this.section = section;
        this.content = content;
    }

    private int section;
    private String content;

}
