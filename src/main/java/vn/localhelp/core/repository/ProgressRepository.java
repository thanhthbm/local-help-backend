package vn.localhelp.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.Progress;

import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByJobApplicationIdOrderByTimestampAsc(Long applicationId);
}
