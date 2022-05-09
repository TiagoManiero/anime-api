package com.tiagomaniero.essentials.repository;

import com.tiagomaniero.essentials.domain.DatabaseUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseUserRepository extends JpaRepository<DatabaseUser, Long> {

    DatabaseUser findByUsername(String username);
}
