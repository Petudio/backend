package kuding.petudio.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Bundle {

    public Bundle(){}

    @Id
    @GeneratedValue
    @Column(name = "bundle_id")
    private Long id;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList<>();

    public void addPicture(Picture picture) {
        pictures.add(picture);
    }
}
