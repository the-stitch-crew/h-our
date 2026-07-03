package stitch.crew.hour.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.category.dto.CategorySaveRequest;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.response.ApiResponse;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<ApiResponse<Void>>  saveCategory (@RequestBody @Valid CategorySaveRequest request) {
        categoryService.save(request);
        return ApiResult.ok(SuccessCode .CATEGORY_CREATED);
    }
}
