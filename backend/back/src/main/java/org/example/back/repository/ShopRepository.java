package org.example.back.repository;

import org.example.back.entity.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ShopRepository extends JpaRepository<ShopOrder, Long> {
    
}
