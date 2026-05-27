package vn.localhelp.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.Category;

/**
 * Repository thao tác bảng categories.
 *
 * JpaRepository đã cung cấp sẵn các hàm CRUD dùng trong chức năng quản lý danh mục.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
