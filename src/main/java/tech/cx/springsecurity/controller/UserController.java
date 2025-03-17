package tech.cx.springsecurity.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import tech.cx.springsecurity.controller.dto.CreateUserDto;
import tech.cx.springsecurity.entities.Role;
import tech.cx.springsecurity.entities.User;
import tech.cx.springsecurity.repository.RoleRepository;
import tech.cx.springsecurity.repository.UserRepository;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserController(
      UserRepository userRepository,
      RoleRepository roleRepository,
      BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  @PostMapping("/users")
  public ResponseEntity<Void> createUser(@RequestBody CreateUserDto createUserDto) {
    // TODO: process POST request

    var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

    var userFromDb = userRepository.findByUsername(createUserDto.username());

    if (userFromDb.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    var user = new User();

    user.setUsername(createUserDto.username());
    user.setPassword(passwordEncoder.encode(createUserDto.password()));
    user.setRoles(Set.of(basicRole));

    userRepository.save(user);

    return ResponseEntity.ok().build();
  }

  @GetMapping("users")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<List<User>> listUser() {
    var users = userRepository.findAll();

    return ResponseEntity.ok(users);
  }

}
