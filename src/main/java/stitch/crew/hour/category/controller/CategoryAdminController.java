package stitch.crew.hour.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponses<Void>>  saveCategory (
            @RequestBody @Valid CategoryRequest request
    ) {
        categoryService.save(request);
        return ApiResult.created(SuccessCode.CATEGORY_CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponses<Void>> updateCategory (@PathVariable Long categoryId,
                                                             @RequestBody @Valid CategoryRequest request) {
        categoryService.updateCategory(categoryId, request);
        return ApiResult.ok(SuccessCode.CATEGORY_UPDATED);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponses<Void>> deleteCategory (@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResult.ok(SuccessCode.CATEGORY_DELETED);
    }
}
