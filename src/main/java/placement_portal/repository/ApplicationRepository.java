package placement_portal.repository;

import placement_portal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByJobId(Long jobId);
    List<Application> findByStudentIdAndJobId(
            Long studentId,
            Long jobId
    );

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);
    void deleteByJobId(Long jobId);
}