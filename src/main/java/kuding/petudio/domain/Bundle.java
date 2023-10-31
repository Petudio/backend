package kuding.petudio.domain;

import kuding.petudio.domain.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "bundle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bundle extends BaseTimeEntity {

    public Bundle(BundleType bundleType) {
        this.likeCount = 0;
        this.isPublic = false;
        this.existAfterPictures = false;
        this.bundleType = bundleType;
    }

    @Id
    @GeneratedValue
    @Column(name = "bundle_id")
    private Long id;

    private String title;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.PERSIST)
    private List<Picture> pictures = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BundleType bundleType;

    @Convert(converter = BooleanToYNConverter.class)
    private boolean isPublic;

    @Convert(converter = BooleanToYNConverter.class)
    private boolean existAfterPictures;

    private int likeCount;

    public void addPicture(Picture picture) {
        pictures.add(picture);
        picture.setBundle(this);
    }

    public void addLikeCount() {
        likeCount++;
    }

    public void changeToPublic(String title) {
        this.isPublic = true;
        this.title = title;
    }

    public void completeGeneratingAfterPictures() {
        this.existAfterPictures = true;
    }
}
