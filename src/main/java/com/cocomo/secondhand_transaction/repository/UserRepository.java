package com.cocomo.secondhand_transaction.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cocomo.secondhand_transaction.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);//로그인시 사용자 확인

    boolean existsByEmail(String email);//회원가입시 중복확인
    Optional<User> findByNickname(String Nickname);


}