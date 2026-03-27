package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.dto.response.AlertCountResponse;
import br.senac.sp.projeto_integrador.dto.response.AlertResponse;
import br.senac.sp.projeto_integrador.mapper.AlertMapper;
import br.senac.sp.projeto_integrador.model.Alert;
import br.senac.sp.projeto_integrador.model.AlertSeverity;
import br.senac.sp.projeto_integrador.model.BeerStage;
import br.senac.sp.projeto_integrador.model.Reading;
import br.senac.sp.projeto_integrador.repository.AlertRepository;
import br.senac.sp.projeto_integrador.repository.ReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AlertService {
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    private final AlertRepository alertRepository;
    private final ReadingRepository readingRepository;

    @Autowired
    public AlertService(AlertRepository alertRepository, ReadingRepository readingRepository) {
        this.alertRepository = alertRepository;
        this.readingRepository = readingRepository;
    }

    public List<AlertResponse> getAll() {
        return alertRepository.findAll().stream().map(alert -> AlertMapper.toAlertResponse(alert)).toList();
    }

    public List<AlertResponse> getRecent() {
        return alertRepository.findByCreatedAtAfterOrderByCreatedAtDesc(OffsetDateTime.now().minusHours(24))
                .stream().map(alert -> AlertMapper.toAlertResponse(alert)).toList();
    }

    public AlertCountResponse getRecentCount() {
        int size = alertRepository.findByCreatedAtAfterOrderByCreatedAtDesc(OffsetDateTime.now().minusHours(24)).size();
        return new AlertCountResponse(size);
    }

    private boolean checkRange(BeerStage stage, double liquidTemp) {
        if (stage == BeerStage.MASHING) {
            return liquidTemp < 62 || liquidTemp > 72;
        } else if (stage == BeerStage.BOILING) {
            return liquidTemp < 95;
        } else if (stage == BeerStage.FERMENTATION) {
            return liquidTemp < 18 || liquidTemp > 24;
        } else if (stage == BeerStage.MATURATION) {
            return liquidTemp > 5;
        }
        return false;
    }

    public void checkThreshold(Reading reading) {
        double liquidTemp = reading.getLiquidTemp();
        if (reading.getStage() == BeerStage.MASHING) { // Mínimo: 62 | Máximo: 72
            if (liquidTemp <= 60) { // alerta: 2 graus abaixo do mínimo
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.WARNING, "Temperature below 62ºC", liquidTemp, 62));
            } else if (liquidTemp >= 74) { // alerta: 2 graus acima do máximo
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.WARNING, "Temperature above 72ºC", liquidTemp, 72));
            }
        } else if (reading.getStage() == BeerStage.BOILING) { // Mínimo: 95 | Máximo: 100
            if (liquidTemp < 95) {
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.WARNING, "Temperature below 95ºC", liquidTemp, 95));
            }
        } else if (reading.getStage() == BeerStage.FERMENTATION) { // Mínimo: 18 | Máximo: 24
            if (liquidTemp <= 15) { // alerta: 3 graus abaixo do mínimo
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.WARNING, "Temperature below 18ºC", liquidTemp, 18));
            } else if (liquidTemp >= 27) { // alerta: 3 graus acima do máximo
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.WARNING, "Temperature above 24ºC", liquidTemp, 24));
            }
        } else if (reading.getStage() == BeerStage.MATURATION) { // Mínimo: 0 | Máximo: 5
            if (liquidTemp > 8) { //
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.WARNING, "Temperature above 5ºC", liquidTemp, 5));
            }
        }

        if (checkRange(reading.getStage(), liquidTemp)) { // checando se a temperatura está fora da faixa por mais de 5 minutos para alerta crítico.
            List<Reading> readings = readingRepository.findByStageOrderByTimestampDesc(reading.getStage());
            OffsetDateTime readingTimestamp = reading.getTimestamp();

            for(Reading r : readings) { // itera a lista de leituras na mesma fase
                if (checkRange(r.getStage(), r.getLiquidTemp())) {
                    readingTimestamp = r.getTimestamp(); // salva a data mais antiga onde a temperatura ficou fora de faixa
                } else {
                    break; // para quando achar uma leitura onde a temperatura estava dentro do esperado
                }
            }

            Duration interval = Duration.between(readingTimestamp, reading.getTimestamp());
            if (interval.toMinutes() >= 5) {
                alertRepository.save(new Alert(reading, "TEMP_OUT_OF_RANGE", AlertSeverity.CRITICAL, "Temperature outside the range for 5 minutes or more.", liquidTemp, 0));
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkLastRequestInterval() {
        readingRepository.findTopByOrderByTimestampDesc().ifPresent(reading -> {
            Duration interval = Duration.between(reading.getTimestamp(), OffsetDateTime.now());
            if (interval.toSeconds() > 60) {
                alertRepository.save(new Alert("SENSOR_FAIL", AlertSeverity.SENSOR_FAIL, "No readings for more than 60 seconds."));
            }
        });
    }

}
