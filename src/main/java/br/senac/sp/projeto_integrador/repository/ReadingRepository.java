package br.senac.sp.projeto_integrador.repository;

import br.senac.sp.projeto_integrador.model.BeerStage;
import br.senac.sp.projeto_integrador.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {

    Optional<Reading> findTopByOrderByTimestampDesc();
    List<Reading> findByStageOrderByTimestampDesc(BeerStage stage);

}