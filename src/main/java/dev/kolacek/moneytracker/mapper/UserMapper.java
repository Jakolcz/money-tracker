package dev.kolacek.moneytracker.mapper;

import dev.kolacek.moneytracker.domain.user.User;
import dev.kolacek.moneytracker.entity.RoleEntity;
import dev.kolacek.moneytracker.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toDomain(UserEntity userEntity);

    default Set<String> mapRoles(Set<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }
}
