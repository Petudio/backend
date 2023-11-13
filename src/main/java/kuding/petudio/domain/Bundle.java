package kuding.petudio.domain;

import kuding.petudio.domain.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "bundle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bundle extends BaseTimeEntity {

    public Bundle(BundleType bundleType) {
        this.likeCount = 0;
        this.isPublic = false;
        this.isTrainingComplete = false;
        this.bundleType = bundleType;
        this.randomName = UUID.randomUUID().toString();
    }

    @Id
    @GeneratedValue
    @Column(name = "bundle_id")
    private Long id;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.PERSIST)
    private List<Picture> pictures = new ArrayList<>();

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.PERSIST)
    private List<Prompt> prompts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BundleType bundleType;
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isPublic;
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isTrainingComplete;
    private String randomName;
    private String title;
    private int likeCount;

    public void addPicture(Picture picture) {
        pictures.add(picture);
        picture.setBundle(this);
    }

    public void addPrompts(Prompt prompt) {
        this.prompts.add(prompt);
        prompt.setBundle(this);
    }

    public void addLikeCount() {
        likeCount++;
    }

    public void changeToPublic(String title) {
        this.isPublic = true;
        this.title = title;
    }

    public void completeTraining() {
        this.isTrainingComplete = true;
    }

    public boolean isTrainingComplete() {
        return isTrainingComplete;
    }
}
