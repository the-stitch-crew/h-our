package stitch.crew.hour.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.product.constant.ProductStatus;

@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name="thumbnail_url",length=255)
    private String thumbnail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(length = 255)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Integer salesCount;

    @Column(nullable = false)
    private Boolean isMain;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    public Product(
            String name,
            Long price,
            String summary,
            String description
    ){
        this.name = name;
        this.price = price;
        this.status = ProductStatus.ACTIVATED;
        this.viewCount = 0L;
        this.salesCount = 0;
        this.isMain = false;
        if (Strings.isNotBlank(summary)) this.summary = summary;
        if (Strings.isNotBlank(description)) this.description = description;
    }

    public void setCategory(Category category){
        this.category = category;
        category.getProducts().add(this);
    }

    public void updateContent(String name, Long price){
        this.name = name;
        this.price = price;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }
    public void increaseSalesCount(){
        this.salesCount++;
    }
    public void setMain(){
        this.isMain = true;
    }
    public void switchStatus(ProductStatus status){
        this.status = status;
    }

}
