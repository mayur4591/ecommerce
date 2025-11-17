package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByName(String name);

    @Query("SELECT c FROM  Category c WHERE c.name=:name AND c.parentCategory.name=:parantCategoryName")
    Category findByNameAndParent(@Param("name") String name,
                                 @Param("parantCategoryName") String parantCategoryName);

}
