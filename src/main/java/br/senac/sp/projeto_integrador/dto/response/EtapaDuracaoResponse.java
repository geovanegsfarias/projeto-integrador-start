package br.senac.sp.projeto_integrador.dto.response;

public record EtapaDuracaoResponse(
        String stage,
        long duracaoRealMinutos,
        long duracaoMinPlanejadaMinutos,
        long duracaoMaxPlanejadaMinutos
) {}