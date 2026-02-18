package vn.localhelp.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.localhelp.core.domain.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

}
