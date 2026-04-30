package br.senac.sp.projeto_integrador.dto.response;

public record AlertasKpiResponse(
        long info,
        long warning,
        long critical,
        long sensorFail
) {
}