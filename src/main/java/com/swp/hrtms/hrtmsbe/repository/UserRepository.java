package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Integer id); // khai

    boolean existsByEmailAndIdNot(String email, Integer id);// khai

    Optional<User> findByEmail(String email);// thien
    // e Thien them method (xu li conflict-giu nguyen method cua 2 nhanh)

    List<User> findByRoleIn(List<String> roles);
}
