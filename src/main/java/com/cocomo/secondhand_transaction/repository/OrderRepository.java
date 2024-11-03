package com.cocomo.secondhand_transaction.repository;

import com.cocomo.secondhand_transaction.entity.Order;
import com.cocomo.secondhand_transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderNum(String orderNumber);
    List<Order> findBySuccessFalseAndNotifiedFalse();
    List<Order> findBySuccessFalseAndNotifiedTrue();
    List<Order> findAllBySeller(User user);
    List<Order> findAllByBuyer(User user);
}
