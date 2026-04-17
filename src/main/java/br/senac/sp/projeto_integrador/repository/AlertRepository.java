package br.senac.sp.projeto_integrador.repository;

import br.senac.sp.projeto_integrador.model.Alert;
import br.senac.sp.projeto_integrador.model.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Existente — usado para retornar a lista de alertas recentes
    List<Alert> findByCreatedAtAfterOrderByCreatedAtDesc(OffsetDateTime since);

    // Fix de desempenho: conta sem carregar a lista inteira
    long countByCreatedAtAfter(OffsetDateTime since);

    // Gráfico de alerta por severidade — uma query só ao invés de 4 chamadas separadas
    @Query("SELECT a.severity, COUNT(a) FROM Alert a GROUP BY a.severity")
    List<Object[]> countGroupBySeverity();
}