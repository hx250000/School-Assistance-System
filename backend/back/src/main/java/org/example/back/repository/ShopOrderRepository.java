package org.example.back.repository;

import org.example.back.entity.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShopOrderRepository extends JpaRepository<ShopOrder, Integer> {

    List<ShopOrder> findAllByOrderByCreatedAtDesc();

    ShopOrder findById(Long orderId);

}
