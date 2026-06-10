package ra.edu.it211_project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.edu.it211_project.entity.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseCode(String courseCode);
    Optional<Course> findByCourseCode(String courseCode);
    Page<Course> findByCourseNameContainingIgnoreCase(String courseName, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    Page<Course> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);
}
