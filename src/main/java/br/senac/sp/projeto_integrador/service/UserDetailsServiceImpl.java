package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.model.UserAuthenticated;
import br.senac.sp.projeto_integrador.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new UserAuthenticated(user))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

}
