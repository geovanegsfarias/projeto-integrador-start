package br.senac.sp.projeto_integrador.configuration;

import br.senac.sp.projeto_integrador.model.User;
import br.senac.sp.projeto_integrador.model.UserRole;
import br.senac.sp.projeto_integrador.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                userRepository.save(
                        new User("admin@gmail.com", encoder.encode("admin123"), UserRole.ROLE_ADMIN)
                );
            }
        };
    }

}
