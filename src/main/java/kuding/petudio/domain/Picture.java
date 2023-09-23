package kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Picture {

    public Picture(Bundle bundle, String originalName, String path, PictureType pictureType) {
        this.bundle = bundle;
        bundle.addPicture(this);

        this.originalName = originalName;
        this.path = path;
        this.pictureType = pictureType;
    }

    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    private String originalName;
    private String path;

    @Enumerated(EnumType.STRING)
    private PictureType pictureType;

}
