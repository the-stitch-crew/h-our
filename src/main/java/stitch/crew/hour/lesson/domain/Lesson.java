package stitch.crew.hour.lesson.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import stitch.crew.hour.common.domain.BaseEntity;

@Entity
@Getter
@Table(name = "lessons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "")
    private Integer price;

    @Column(nullable = false)
    private Integer duration;

    public  Lesson(String name, Integer price, Integer duration) {
        this.name = name;
        this.price = price;
        this.duration = duration;
    }
}
