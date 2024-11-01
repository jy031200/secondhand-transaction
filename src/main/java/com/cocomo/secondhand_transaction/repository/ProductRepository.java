package com.cocomo.secondhand_transaction.repository;

import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.entity.constant.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 상품 목록 조회
    Optional<Product> findProductByPdNum(String pdNum);

    Optional<Product> findProductByPdName(String pd_name);
}