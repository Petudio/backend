package kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bundle {

    public Bundle(BundleType bundleType) {
        like = 0;
        this.bundleType = bundleType;
    }

    @Id
    @GeneratedValue
    @Column(name = "bundle_id")
    private Long id;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BundleType bundleType;

    private int like;

    public void addPicture(Picture picture) {
        pictures.add(picture);
        picture.setBundle(this);
    }
}
