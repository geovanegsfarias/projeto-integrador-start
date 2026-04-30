package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.dto.response.*;
import br.senac.sp.projeto_integrador.model.AlertSeverity;
import br.senac.sp.projeto_integrador.model.BeerStage;
import br.senac.sp.projeto_integrador.repository.AlertRepository;
import br.senac.sp.projeto_integrador.repository.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class KpiService {

    private static final double POTENCIA_KW = 1.0;

    private static final Map<BeerStage, long[]> DURACAO_PLANEJADA = Map.of(
            BeerStage.MASHING, new long[]{60L, 60L},
            BeerStage.BOILING, new long[]{60L, 90L},
            BeerStage.FERMENTATION, new long[]{10080L, 20160L},
            BeerStage.MATURATION, new long[]{20160L, 43200L}
    );
    private final ReadingRepository readingRepository;
    private final AlertRepository alertRepository;

    @Autowired
    public KpiService(ReadingRepository readingRepository, AlertRepository alertRepository) {
        this.readingRepository = readingRepository;
        this.alertRepository = alertRepository;
    }

    public DesvioPadraoKpiResponse getDesvioPadrao() {
        Double result = readingRepository.findDesvioPadrao();
        return new DesvioPadraoKpiResponse(result != null ? result : 0.0);
    }

    public ConformidadeKpiResponse getConformidade() {
        Double result = readingRepository.findConformidadePercentual();
        return new ConformidadeKpiResponse(result != null ? result : 0.0);
    }

    public EnergiaKpiResponse getEnergia() {
        double totalHoras = 0;
        for (Object[] row : readingRepository.findDuracaoPorEtapa()) {
            OffsetDateTime min = (OffsetDateTime) row[1];
            OffsetDateTime max = (OffsetDateTime) row[2];
            totalHoras += Duration.between(min, max).toMinutes() / 60.0;
        }
        return new EnergiaKpiResponse(POTENCIA_KW * totalHoras);
    }

    public List<TemperaturaAgregadaResponse> getHistoricoTemperatura() {
        Map<Integer, Object[]> porJanela = new HashMap<>();
        for (Object[] row : readingRepository.findMediaTemperaturaAgregada24h()) {
            int janela = ((Number) row[0]).intValue();
            porJanela.put(janela, row);
        }

        List<TemperaturaAgregadaResponse> resultado = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            String label = String.format("%02dh", h);
            Object[] row = porJanela.get(h);
            if (row != null) {
                resultado.add(new TemperaturaAgregadaResponse(
                        label,
                        row[1] != null ? ((Number) row[1]).doubleValue() : null,
                        row[2] != null ? ((Number) row[2]).doubleValue() : null
                ));
            } else {
                resultado.add(new TemperaturaAgregadaResponse(label, null, null));
            }
        }
        return resultado;
    }

    public List<MediaPorEtapaResponse> getMediaTemperaturaPorEtapa() {
        Map<BeerStage, Double> porEtapa = new EnumMap<>(BeerStage.class);
        for (Object[] row : readingRepository.findAvgLiquidTempByStage()) {
            porEtapa.put((BeerStage) row[0], (Double) row[1]);
        }
        return Arrays.stream(BeerStage.values())
                .map(stage -> new MediaPorEtapaResponse(
                        stage.getStage(),
                        porEtapa.getOrDefault(stage, null)
                ))
                .toList();
    }

    public AlertasKpiResponse getAlertasPorSeveridade() {
        Map<AlertSeverity, Long> counts = new EnumMap<>(AlertSeverity.class);
        for (Object[] row : alertRepository.countGroupBySeverity()) {
            counts.put((AlertSeverity) row[0], (Long) row[1]);
        }
        return new AlertasKpiResponse(
                counts.getOrDefault(AlertSeverity.INFO, 0L),
                counts.getOrDefault(AlertSeverity.WARNING, 0L),
                counts.getOrDefault(AlertSeverity.CRITICAL, 0L),
                counts.getOrDefault(AlertSeverity.SENSOR_FAIL, 0L)
        );
    }

    public List<EtapaDuracaoResponse> getEtapasDuracao() {
        Map<BeerStage, Long> duracaoReal = new EnumMap<>(BeerStage.class);
        for (Object[] row : readingRepository.findDuracaoPorEtapa()) {
            BeerStage stage = (BeerStage) row[0];
            OffsetDateTime min = (OffsetDateTime) row[1];
            OffsetDateTime max = (OffsetDateTime) row[2];
            duracaoReal.put(stage, Duration.between(min, max).toMinutes());
        }
        return Arrays.stream(BeerStage.values())
                .map(stage -> {
                    long[] planejado = DURACAO_PLANEJADA.get(stage);
                    return new EtapaDuracaoResponse(
                            stage.getStage(),
                            duracaoReal.getOrDefault(stage, 0L),
                            planejado[0],
                            planejado[1]
                    );
                })
                .toList();
    }
}