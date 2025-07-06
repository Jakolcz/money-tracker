package dev.kolacek.moneytracker.service;

import dev.kolacek.moneytracker.domain.user.User;
import dev.kolacek.moneytracker.entity.RoleEntity;
import dev.kolacek.moneytracker.entity.UserEntity;
import dev.kolacek.moneytracker.mapper.UserMapper;
import dev.kolacek.moneytracker.repository.RoleRepository;
import dev.kolacek.moneytracker.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;
    @Inject
    UserMapper userMapper;
    @Inject
    RoleRepository roleRepository;

    @WithTransaction
    public Uni<User> createUser(User user, String rawPassword, List<String> roles) {
        return roleRepository.findByNames(user.getRoles())
                .flatMap(roleEntities -> {
                    if (roleEntities.isEmpty()) {
                        return Uni.createFrom().failure(new IllegalArgumentException("No valid roles found"));
                    }

                    return Uni.createFrom().item(toEntity(user, roleEntities));
                })
                .flatMap(userRepository::persistAndFlush)
                .map(userMapper::toDomain);
    }

    private UserEntity toEntity(User user, List<RoleEntity> roleEntities) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setEmail(user.getEmail());
        userEntity.setRoles(new HashSet<>(roleEntities));
        userEntity.setPassword(BcryptUtil.bcryptHash(user.getPassword()));

        return userEntity;
    }
}
