package br.senac.sp.projeto_integrador.mapper;

import br.senac.sp.projeto_integrador.dto.response.AlertResponse;
import br.senac.sp.projeto_integrador.model.Alert;

public class AlertMapper {

    public static AlertResponse toAlertResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                (alert.getReading() != null) ? alert.getReading().getId() : null,
                alert.getType(),
                alert.getSeverity(),
                alert.getMessage(),
                alert.getValue(),
                alert.getThreshold(),
                alert.getCreatedAt());
    }

}
