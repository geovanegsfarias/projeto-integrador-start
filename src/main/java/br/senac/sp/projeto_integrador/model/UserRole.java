package br.senac.sp.projeto_integrador.model;

public enum UserRole {
    ROLE_ADMIN("admin");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}