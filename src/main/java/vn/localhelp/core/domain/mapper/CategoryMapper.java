package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.request.category.CategoryRequest;
import vn.localhelp.core.domain.response.category.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryResponse toResponse(Category category);

  @Mapping(target = "id", ignore = true)
  Category toEntity(CategoryRequest categoryRequest);

  @Mapping(target = "id", ignore = true)
  void updateEntity(@MappingTarget Category category, CategoryRequest categoryRequest);
}
