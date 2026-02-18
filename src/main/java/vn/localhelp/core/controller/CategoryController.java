package vn.localhelp.core.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.request.category.CategoryRequest;
import vn.localhelp.core.domain.response.category.CategoryResponse;
import vn.localhelp.core.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    List<CategoryResponse> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
    CategoryResponse category = categoryService.getCategoryById(Long.parseLong(id));
    return ResponseEntity.ok(category);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
    return ResponseEntity.ok(categoryService.create(request));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable Long id,
      @Valid @RequestBody CategoryRequest request) {
    return ResponseEntity.ok(categoryService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
