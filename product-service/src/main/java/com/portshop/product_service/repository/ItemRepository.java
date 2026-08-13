package com.portshop.product_service.repository;

import com.portshop.product_service.model.Item;
import com.portshop.product_service.productEnum.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    boolean existsBySize(Size size);

    Optional<Item> findBySize(Size size);
}
