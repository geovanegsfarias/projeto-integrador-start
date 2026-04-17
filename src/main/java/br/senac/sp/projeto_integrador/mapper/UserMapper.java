package br.senac.sp.projeto_integrador.mapper;

import br.senac.sp.projeto_integrador.dto.response.UserResponse;
import br.senac.sp.projeto_integrador.model.User;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

}