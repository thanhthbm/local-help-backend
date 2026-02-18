package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.mapper.CategoryMapper;
import vn.localhelp.core.domain.request.category.CategoryRequest;
import vn.localhelp.core.domain.response.category.CategoryResponse;
import vn.localhelp.core.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public List<CategoryResponse> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();

    return categories.stream()
        .map(categoryMapper::toResponse).collect(Collectors.toList());
  }

  public CategoryResponse getCategoryById(long Id) {
    return categoryMapper.toResponse(
        categoryRepository.findById(Id).orElseThrow(
            () -> new RuntimeException("Category not found with id: " + Id)
        )
    );
  }

  @Transactional
  public CategoryResponse create(CategoryRequest request) {
    Category category = categoryMapper.toEntity(request);
    Category savedCategory = categoryRepository.save(category);
    return categoryMapper.toResponse(savedCategory);
  }

  @Transactional
  public CategoryResponse update(Long id, CategoryRequest request) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

    categoryMapper.updateEntity(category, request);

    Category updatedCategory = categoryRepository.save(category);

    return categoryMapper.toResponse(updatedCategory);
  }

  @Transactional
  public void delete(Long id) {
    if (!categoryRepository.existsById(id)) {
      throw new RuntimeException("Category not found with id: " + id);
    }
    categoryRepository.deleteById(id);
  }
}
