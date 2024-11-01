package com.cocomo.secondhand_transaction.repository;

import com.cocomo.secondhand_transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname); // nickname으로 사용자 조회
}