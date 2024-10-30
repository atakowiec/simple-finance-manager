package pl.pollub.backend.categories;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    boolean existsByName(String name);}
