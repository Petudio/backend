package Kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Post {

    public Post(){}

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList<>();

    public void addPicture(Picture picture) {
        pictures.add(picture);
    }
}
