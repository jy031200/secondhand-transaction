package com.cocomo.secondhand_transaction.repository;

import com.cocomo.secondhand_transaction.entity.Review;
import com.cocomo.secondhand_transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {


    List<Review> findByBuyer(User buyer); // 사용자가 작성한 리뷰 조회

    List<Review> findBySeller(User seller);
}
