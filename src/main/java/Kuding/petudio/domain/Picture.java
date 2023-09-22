package Kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Picture {

    public Picture(Post post, String originalName, String path, PictureType pictureType) {
        this.post = post;
        post.addPicture(this);

        this.originalName = originalName;
        this.path = path;
        this.pictureType = pictureType;
    }

    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String originalName;
    private String path;

    @Enumerated(EnumType.STRING)
    private PictureType pictureType;

}
