package com.example.expense.repository;

import com.example.expense.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Optional;
import com.google.inject.Inject;

public class CategoryRepository extends BaseRepository<Category> {

    @Inject
    public CategoryRepository(EntityManager entityManager) {
        super(entityManager, Category.class);
    }
    
    public Optional<Category> findByName(String name) {
        try {
            TypedQuery<Category> query = entityManager.createQuery(
                "SELECT c FROM Category c WHERE c.name = :name", Category.class);
            query.setParameter("name", name);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
