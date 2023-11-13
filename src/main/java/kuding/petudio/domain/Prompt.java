package kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prompt extends BaseTimeEntity {
    public Prompt(int section, String content) {
        this.section = section;
        this.content = content;
    }

    @Id
    @GeneratedValue
    public Long id;

    private int section;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
