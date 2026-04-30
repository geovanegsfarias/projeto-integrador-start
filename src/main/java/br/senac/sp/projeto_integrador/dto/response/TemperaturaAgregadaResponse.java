package br.senac.sp.projeto_integrador.dto.response;

public record TemperaturaAgregadaResponse(
        String janela,          // "00h", "04h", "08h", "12h", "16h", "20h"
        Double mediaLiquidTemp, // Double (nullable) — janela sem dados retorna null
        Double mediaAmbientTemp
) {
}