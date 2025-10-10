package courseitda.category.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import courseitda.category.domain.Category;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {
}

