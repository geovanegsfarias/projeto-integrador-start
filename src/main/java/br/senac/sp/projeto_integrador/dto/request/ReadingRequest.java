package br.senac.sp.projeto_integrador.dto.request;

import br.senac.sp.projeto_integrador.model.BeerStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReadingRequest(@NotNull(message = "Ambient Temperature may not be null.") Double ambientTemp,
                             @NotNull(message = "Liquid Temperature may not be null.") Double liquidTemp,
                             @NotNull(message = "Humidity may not be null.") Double humidity,
                             @NotBlank(message = "Device Id may not be blank.") String deviceId)
{
}