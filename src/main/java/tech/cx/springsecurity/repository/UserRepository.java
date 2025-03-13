package tech.cx.springsecurity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.cx.springsecurity.controller.dto.LoginRequest;
import tech.cx.springsecurity.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

}
