package courseitda.category.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import courseitda.category.domain.CategoryPlace;

public interface JpaCategoryPlaceRepository extends JpaRepository<CategoryPlace, Long> {

    List<CategoryPlace> findAllByCategoryId(Long categoryId);
}
