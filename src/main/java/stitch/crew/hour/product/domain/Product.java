package stitch.crew.hour.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.product.constant.ProductStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "products")
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

    private LocalDate lastErolledToMain;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<CartProduct> cartProduct = new ArrayList<>();

    public Product(
            String name,
            Long price,
            String summary,
            String description,
            Category category
    ){
        this.name = name;
        this.price = price;
        this.status = ProductStatus.ACTIVATED;
        this.viewCount = 0L;
        this.salesCount = 0;
        this.isMain = false;
        if (Strings.isNotBlank(summary)) this.summary = summary;
        if (Strings.isNotBlank(description)) this.description = description;
        setCategory(category);
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
        lastErolledToMain = LocalDate.now();
    }

    public void unsetMain(){
        this.isMain = false;
        lastErolledToMain = null;
    }
    public void switchStatus(ProductStatus status){
        this.status = status;
    }
}
