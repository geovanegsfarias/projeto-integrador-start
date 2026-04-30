package br.senac.sp.projeto_integrador.repository;

import br.senac.sp.projeto_integrador.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByCreatedAtAfterOrderByCreatedAtDesc(OffsetDateTime since);

    long countByCreatedAtAfter(OffsetDateTime since);

    @Query("SELECT a.severity, COUNT(a) FROM Alert a GROUP BY a.severity")
    List<Object[]> countGroupBySeverity();
}