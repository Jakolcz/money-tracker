package dev.kolacek.moneytracker.repository;

import dev.kolacek.moneytracker.entity.UserEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Uni<UserEntity> findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Uni<Boolean> existsByEmail(String email) {
        return find("email", email)
                .count()
                .map(count -> count > 0);
    }
}
