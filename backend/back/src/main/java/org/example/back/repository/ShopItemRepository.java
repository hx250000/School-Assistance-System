package org.example.back.repository;

import org.example.back.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    @Modifying
    @Query("""
         update ShopItem s
         set s.stock=s.stock-1
         where s.id=:shopItemId
         and s.stock>0
    """
    )
    int decreaseIfNotEmpty(@Param("shopItemId") long shopItemId);

    @Modifying
    @Query("""
         update ShopItem s
         set s.stock=s.stock+:amount
         where s.id=:shopItemId
    """
    )
    int increase(@Param("shopItemId")long shopItemId, @Param("amount")long amount);
}
