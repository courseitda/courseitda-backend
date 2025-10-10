package courseitda.category.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPlaceRepository extends JpaRepository<CategoryPlace, Long> {

    List<CategoryPlace> findAllByCategoryId(Long categoryId);
}
