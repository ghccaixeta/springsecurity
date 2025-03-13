package tech.cx.springsecurity.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;
import tech.cx.springsecurity.entities.Role;
import tech.cx.springsecurity.entities.User;
import tech.cx.springsecurity.repository.RoleRepository;
import tech.cx.springsecurity.repository.UserRepository;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

  private RoleRepository roleRepository;
  private UserRepository userRepository;
  private BCryptPasswordEncoder passwordEncoder;

  public AdminUserConfig(
    RoleRepository roleRepository,
    UserRepository userRepository,
    BCryptPasswordEncoder passwordEncoder
  ){
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    
    var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

    var userAdmin = userRepository.findByUsername("admin");

    userAdmin.ifPresentOrElse(
      (user) -> {
        System.out.println("admin ja existe");
      }, 
      () -> {
        var user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("123456789"));
        user.setRoles(Set.of(roleAdmin));

        userRepository.save(user);
      }
      );

  }

}
