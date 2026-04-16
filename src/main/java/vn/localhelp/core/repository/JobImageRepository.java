package vn.localhelp.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.JobImage;
import vn.localhelp.core.util.constant.ImageType;
import java.util.List;

@Repository
public interface JobImageRepository extends JpaRepository<JobImage, Long> {
    List<JobImage> findByJobIdAndImageType(Long jobId, ImageType imageType);
}
