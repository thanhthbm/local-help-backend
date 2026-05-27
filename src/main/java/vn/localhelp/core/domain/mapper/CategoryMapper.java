package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.request.category.CategoryRequest;
import vn.localhelp.core.domain.response.category.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  /** Map entity Category sang DTO trả về cho app/admin. */
  CategoryResponse toResponse(Category category);

  /** Tạo entity Category mới từ request admin gửi lên. */
  @Mapping(target = "id", ignore = true)
  Category toEntity(CategoryRequest categoryRequest);

  /** Cập nhật entity hiện có, giữ nguyên id để tránh ghi đè khóa chính. */
  @Mapping(target = "id", ignore = true)
  void updateEntity(@MappingTarget Category category, CategoryRequest categoryRequest);
}
