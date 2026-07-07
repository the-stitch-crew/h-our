package stitch.crew.hour.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.category.dto.CategoryResponse;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponses<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> response = categoryService.getCategories();
        return ApiResult.ok(SuccessCode.CATEGORY_READ,  response);
    }
}
