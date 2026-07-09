package stitch.crew.hour.category.dto;

import java.time.LocalDateTime;

import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.product.constant.ProductStatus;

public record AdminCategoryDetailResponse(
	Long categoryId,
	String name,
	String thumbnail,
	Long totalProductCount,
	Long activeProductCount,
	Long soldOutProductCount,
	Long deactivatedProductCount,
	Long deletedProductCount,
	Long mainProductCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	LocalDateTime deletedAt
) {
	public static AdminCategoryDetailResponse from(Category category) {
		return new AdminCategoryDetailResponse(
			category.getId(),
			category.getName(),
			category.getThumbnail(),
			countNotDeletedProducts(category),
			countProductsByStatus(category, ProductStatus.ACTIVATED),
			countProductsByStatus(category, ProductStatus.SOLD_OUT),
			countProductsByStatus(category, ProductStatus.DEACTIVATED),
			countProductsByStatus(category, ProductStatus.DELETED),
			countMainProducts(category),
			category.getCreatedAt(),
			category.getUpdatedAt(),
			category.getDeletedAt()
		);
	}

	private static Long countNotDeletedProducts(Category category) {
		return category.getProducts().stream()
			.filter(product -> product.getStatus() != ProductStatus.DELETED)
			.count();
	}

	private static Long countProductsByStatus(
		Category category,
		ProductStatus status
	) {
		return category.getProducts().stream()
			.filter(product -> product.getStatus() == status)
			.count();
	}

	private static Long countMainProducts(Category category) {
		return category.getProducts().stream()
			.filter(product -> product.getIsMain())
			.filter(product -> product.getStatus() != ProductStatus.DELETED)
			.count();
	}
}
