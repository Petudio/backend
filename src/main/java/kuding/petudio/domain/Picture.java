package kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "picture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Picture extends BaseTimeEntity {

    public Picture(String originalName, String storedName, PictureType pictureType, int section) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.pictureType = pictureType;
        this.section = section;
    }

    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    private String originalName;
    private String storedName;// s3에서의 파일 이름, uuid이용해 생성?

    @Enumerated(EnumType.STRING)
    private PictureType pictureType;
    private int section;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
