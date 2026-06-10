package ra.edu.it211_project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.edu.it211_project.entity.Submission;
import ra.edu.it211_project.entity.SubmissionStatus;

import java.util.List;
import java.util.Optional;
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Page<Submission> findByStudentId(Long studentId, Pageable pageable);
    Page<Submission> findByCourseId(Long courseId, Pageable pageable);
    Optional<Submission> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Submission> findByCourseIdAndStatus(Long courseId, SubmissionStatus status);
}
