package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

  @Cacheable(
      value = "categories",
      key = "'all'",
      unless = "#result == null || #result.isEmpty()"

  )
  public List<CategoryResponse> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();

    return categories.stream()
        .map(categoryMapper::toResponse).collect(Collectors.toList());
  }

  @Cacheable(
      value = "categories",
      key = "#id",
      unless = "#result == null"
  )
  public CategoryResponse getCategoryById(long id) {
    return categoryMapper.toResponse(
        categoryRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Category not found with id: " + id)
        )
    );
  }


  @Transactional
  @CacheEvict(
      value = "categories",
      key = "'all'"
  )
  public CategoryResponse create(CategoryRequest request) {
    Category category = categoryMapper.toEntity(request);
    Category savedCategory = categoryRepository.save(category);
    return categoryMapper.toResponse(savedCategory);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(
          value = "categories",
          key = "#id"
      ),
      @CacheEvict(
          value = "categories",
          key = "'all'")
  })
  public CategoryResponse update(Long id, CategoryRequest request) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

    categoryMapper.updateEntity(category, request);

    Category updatedCategory = categoryRepository.save(category);

    return categoryMapper.toResponse(updatedCategory);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(
          value = "categories",
          key = "#id"
      ),
      @CacheEvict(
          value = "categories",
          key = "'all'")
  })
  public void delete(Long id) {
    if (!categoryRepository.existsById(id)) {
      throw new RuntimeException("Category not found with id: " + id);
    }
    categoryRepository.deleteById(id);
  }
}
