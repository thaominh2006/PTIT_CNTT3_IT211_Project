package ra.edu.it211_project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.edu.it211_project.entity.Material;
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Page<Material> findByCourseId(Long courseId, Pageable pageable);
}
