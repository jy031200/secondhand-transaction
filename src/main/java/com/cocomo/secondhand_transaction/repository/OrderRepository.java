package com.cocomo.secondhand_transaction.repository;

import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.entity.Product;
import com.cocomo.secondhand_transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    //리뷰 조회
    // 주문 테이블의 주문번호로 주문 조회
    Optional<Order> findById(int id);

    Optional<Product> findOrderByOdNum(String num);

    // 상품 번호로 주문 조회
    List<Order> findByPdNum(String pd_num);

    // 구매자(User)로 주문 조회
    List<Order> findByBuyerId(User buyer);

    // 판매자(User)로 주문 조회
    List<Order> findBySellerId(User seller);

    Optional<User> findBySellerEmail(String email);

}
