package br.senac.sp.projeto_integrador.dto.response;

import br.senac.sp.projeto_integrador.model.BeerStage;

import java.time.OffsetDateTime;

public record ReadingResponse(Long id,
                              OffsetDateTime timestamp,
                              double ambientTemp,
                              double liquidTemp,
                              double humidity,
                              BeerStage stage,
                              String deviceId) {
}