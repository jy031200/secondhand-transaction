package com.cocomo.secondhand_transaction.repository;

import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import com.cocomo.secondhand_transaction.entity.constant.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    // 특정 상품 눌렀을 때
    Optional<Product> findProductByPd_num(String num);

//    // 상품 목록 조회
//    // 1. 특정 유저의 상품 조회
//    List<Product> findProductByUser(User user);
//    // 2. 상품명으로 조회
//    List<Product> findProductByPdName(String name);
//    // 3. 상품 카테고리로 조회
//    List<Product> findProductByCategory(Category category);
//    // 4. 상품 위치로 조회
//    List<Product> findProductByLocation(String location);


}
