package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByNameOrderByNameAsc(String name);

    boolean existsByName(String category);
}