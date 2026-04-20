package br.senac.sp.projeto_integrador.repository;

import br.senac.sp.projeto_integrador.model.BeerStage;
import br.senac.sp.projeto_integrador.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {

    Optional<Reading> findTopByOrderByTimestampDesc();
    List<Reading> findByStageOrderByTimestampDesc(BeerStage stage);

    @Query("SELECT r.stage, AVG(r.liquidTemp) FROM Reading r GROUP BY r.stage")
    List<Object[]> findAvgLiquidTempByStage();

    @Query(value = "SELECT STDDEV(liquid_temp)::float8 FROM readings", nativeQuery = true)
    Double findDesvioPadrao();

    // Conformidade
    @Query(value = """
            SELECT
              COUNT(CASE
                WHEN stage = 'MASHING'      AND liquid_temp BETWEEN 62 AND 72 THEN 1
                WHEN stage = 'BOILING'      AND liquid_temp >= 95              THEN 1
                WHEN stage = 'FERMENTATION' AND liquid_temp BETWEEN 18 AND 24  THEN 1
                WHEN stage = 'MATURATION'   AND liquid_temp <= 5               THEN 1
              END) * 100.0 / NULLIF(COUNT(*), 0)
            FROM readings
            """, nativeQuery = true)
    Double findConformidadePercentual();

    // Duração por etapa
    @Query("SELECT r.stage, MIN(r.timestamp), MAX(r.timestamp) FROM Reading r GROUP BY r.stage")
    List<Object[]> findDuracaoPorEtapa();

    @Query(value = """
            SELECT
              EXTRACT(HOUR FROM timestamp AT TIME ZONE 'America/Sao_Paulo')::int AS janela,
              AVG(liquid_temp)  AS media_liquid,
              AVG(ambient_temp) AS media_ambient
            FROM readings
            WHERE (timestamp AT TIME ZONE 'America/Sao_Paulo')::date
                  = (NOW() AT TIME ZONE 'America/Sao_Paulo')::date
            GROUP BY janela
            ORDER BY janela
            """, nativeQuery = true)
    List<Object[]> findMediaTemperaturaAgregada24h();
}