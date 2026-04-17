package br.senac.sp.projeto_integrador.dto.response;

import br.senac.sp.projeto_integrador.model.UserRole;

public record UserResponse(
        Long id,
        String email,
        UserRole role) {
}