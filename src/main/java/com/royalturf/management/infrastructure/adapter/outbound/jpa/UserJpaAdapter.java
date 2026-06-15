package com.royalturf.management.infrastructure.adapter.outbound.jpa;

import com.royalturf.management.domain.model.User;
import com.royalturf.management.domain.port.outbound.UserRepositoryPort;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.entity.UserJpaEntity;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.mapper.JpaMapper;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserJpaAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    public UserJpaAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = JpaMapper.toEntity(user);
        UserJpaEntity savedEntity = springDataUserRepository.save(entity);
        return JpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserRepository.findById(id)
                .map(JpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByUsername(username)
                .map(JpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
                .map(JpaMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }
}
