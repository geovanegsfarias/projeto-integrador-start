package br.senac.sp.projeto_integrador.dto.response;

import br.senac.sp.projeto_integrador.model.AlertSeverity;

import java.time.OffsetDateTime;

public record AlertResponse(Long id, Long readingId, String type, AlertSeverity severity, String message, double value,
                            double threshold, OffsetDateTime createdAt) {
}