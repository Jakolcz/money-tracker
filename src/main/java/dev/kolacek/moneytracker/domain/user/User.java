package dev.kolacek.moneytracker.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String email;
    private String password;
    private Set<String> roles;
}
