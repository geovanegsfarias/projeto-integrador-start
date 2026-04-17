package br.senac.sp.projeto_integrador.dto.response;

public record MediaPorEtapaResponse(
        String stage,
        Double media  // null quando a etapa ainda não tem leituras
) {}