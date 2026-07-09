package stitch.crew.hour.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.category.dto.AdminCategoryDetailResponse;
import stitch.crew.hour.category.dto.AdminCategorySearchResponse;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.service.CategoryAdminService;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final CategoryAdminService categoryAdminService;

    @GetMapping
    public ResponseEntity<ApiResponses<Page<AdminCategorySearchResponse>>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") Boolean includeDeleted
    ) {
        return ApiResult.ok(
                SuccessCode.CATEGORY_READ,
                categoryAdminService.getCategories(
                        page,
                        size,
                        keyword,
                        includeDeleted
                )
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponses<AdminCategoryDetailResponse>> getCategory(
            @PathVariable Long categoryId
    ) {
        return ApiResult.ok(
                SuccessCode.CATEGORY_READ,
                categoryAdminService.getCategory(categoryId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponses<Void>>  saveCategory (
            @RequestPart @Valid CategoryRequest request,
            @RequestPart(required = false) MultipartFile file
            ) {
        categoryService.save(request, file);
        return ApiResult.created(SuccessCode.CATEGORY_CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponses<Void>> updateCategory (@PathVariable Long categoryId,
                                                              @RequestPart @Valid CategoryRequest request,
                                                              @RequestPart(required = false) MultipartFile file) {
        categoryService.updateCategory(categoryId, request, file);
        return ApiResult.ok(SuccessCode.CATEGORY_UPDATED);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponses<Void>> deleteCategory (@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResult.ok(SuccessCode.CATEGORY_DELETED);
    }
}
