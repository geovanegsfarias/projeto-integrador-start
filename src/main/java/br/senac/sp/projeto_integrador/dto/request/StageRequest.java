package br.senac.sp.projeto_integrador.dto.request;

import br.senac.sp.projeto_integrador.model.BeerStage;
import jakarta.validation.constraints.NotNull;

public record StageRequest(
        @NotNull(message = "Beer Stage may not be null.") BeerStage stage
) {
}