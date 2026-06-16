package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Khải: Repository thao tác dữ liệu tài khoản dùng cho API profile chủ ngựa.
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsernameAndIdNot(String username, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);
}
