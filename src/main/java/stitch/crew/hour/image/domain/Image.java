package stitch.crew.hour.image.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;

@Entity
@Getter
@Table(name="images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String saveUrl;

    @Column(nullable = false)
    private Integer sortOrder;

    public Image(String saveUrl, Integer sortOrder) {
        this.saveUrl = saveUrl;
        this.sortOrder = sortOrder;
    }

}
