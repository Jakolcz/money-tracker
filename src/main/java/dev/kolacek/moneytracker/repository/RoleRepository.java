package dev.kolacek.moneytracker.repository;

import dev.kolacek.moneytracker.entity.RoleEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<RoleEntity> {

    public Uni<RoleEntity> findByName(String name) {
        return find("name", name).firstResult();
    }

    public Uni<List<RoleEntity>> findByNames(Collection<String> names) {
        if (names == null || names.isEmpty()) {
            return Uni.createFrom().item(List.of());
        }

        return find("name in ?1", names).list();
    }
}
