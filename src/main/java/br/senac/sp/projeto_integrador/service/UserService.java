package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.dto.response.UserResponse;
import br.senac.sp.projeto_integrador.mapper.UserMapper;
import br.senac.sp.projeto_integrador.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUser(Authentication authentication) {
        return UserMapper.toUserResponse(
                userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found."))
        );
    }

}