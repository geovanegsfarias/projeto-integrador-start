package br.senac.sp.projeto_integrador.repository;

import br.senac.sp.projeto_integrador.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByCreatedAtAfterOrderByCreatedAtDesc(OffsetDateTime since);
}