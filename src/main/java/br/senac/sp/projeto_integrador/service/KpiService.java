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

    private final ReadingRepository readingRepository;
    private final AlertRepository alertRepository;

    // Potência do forno: P = V * I = 220V, P = 1000W = 1kW
    // Fórmula: E = P * T  →  kWh = 1.0 kW × horas ligado
    private static final double POTENCIA_KW = 1.0;

    // Range planejado por etapa [minutos_min, minutos_max]
    // Mashing: 60min | Boiling: 60-90min | Fermentation: 7-14 dias | Maturation: 14-30 dias
    private static final Map<BeerStage, long[]> DURACAO_PLANEJADA = Map.of(
            BeerStage.MASHING,      new long[]{ 60L,    60L    },
            BeerStage.BOILING,      new long[]{ 60L,    90L    },
            BeerStage.FERMENTATION, new long[]{ 10080L, 20160L },
            BeerStage.MATURATION,   new long[]{ 20160L, 43200L }
    );

    @Autowired
    public KpiService(ReadingRepository readingRepository, AlertRepository alertRepository) {
        this.readingRepository = readingRepository;
        this.alertRepository = alertRepository;
    }

    // -------------------------------------------------------------------------
    // Desvio padrão — calculado pelo banco
    // -------------------------------------------------------------------------
    public DesvioPadraoKpiResponse getDesvioPadrao() {
        Double result = readingRepository.findDesvioPadrao();
        return new DesvioPadraoKpiResponse(result != null ? result : 0.0);
    }

    // -------------------------------------------------------------------------
    // Conformidade — percentual calculado pelo banco em uma única query
    // -------------------------------------------------------------------------
    public ConformidadeKpiResponse getConformidade() {
        Double result = readingRepository.findConformidadePercentual();
        return new ConformidadeKpiResponse(result != null ? result : 0.0);
    }

    // -------------------------------------------------------------------------
    // Energia — E = P × T, com P = 1kW (forno 220V / 1000W)
    // Soma o tempo total de todas as etapas com leituras registradas
    // -------------------------------------------------------------------------
    public EnergiaKpiResponse getEnergia() {
        double totalHoras = 0;
        for (Object[] row : readingRepository.findDuracaoPorEtapa()) {
            OffsetDateTime min = (OffsetDateTime) row[1];
            OffsetDateTime max = (OffsetDateTime) row[2];
            totalHoras += Duration.between(min, max).toMinutes() / 60.0;
        }
        return new EnergiaKpiResponse(POTENCIA_KW * totalHoras);
    }

    // -------------------------------------------------------------------------
    // Gráfico de linha 24h — janelas de 1h (0..23), banco agrega a média
    // Sempre retorna as 24 horas; horas sem dados chegam com null
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Gráfico de coluna — média por etapa
    // Garante as 4 etapas no retorno mesmo sem leituras (null = sem dados ainda)
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Gráfico de colunas — alertas por severidade
    // Uma query GROUP BY no banco ao invés de chamadas separadas
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Gráfico real vs planejado — sempre retorna as 4 etapas
    // Etapas sem leituras ainda aparecem com duracaoReal = 0
    // -------------------------------------------------------------------------
    public List<EtapaDuracaoResponse> getEtapasDuracao() {
        Map<BeerStage, Long> duracaoReal = new EnumMap<>(BeerStage.class);
        for (Object[] row : readingRepository.findDuracaoPorEtapa()) {
            BeerStage stage    = (BeerStage) row[0];
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