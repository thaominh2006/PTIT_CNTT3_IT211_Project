package ra.edu.it211_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.it211_project.dto.response.MaterialResponse;

public interface MaterialService {
    Page<MaterialResponse> getMaterialsByCourse(Long courseId, Pageable pageable);
}