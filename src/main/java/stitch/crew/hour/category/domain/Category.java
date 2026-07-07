package stitch.crew.hour.category.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.product.domain.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    @Setter
    private String name;

    @Setter
    private String thumbnail;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public void addProduct(Product product){
        this.products.add(product);
    }

    public void update(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }
}
