package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.configuration.StageConfig;
import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import br.senac.sp.projeto_integrador.dto.response.ReadingResponse;
import br.senac.sp.projeto_integrador.mapper.ReadingMapper;
import br.senac.sp.projeto_integrador.model.Reading;
import br.senac.sp.projeto_integrador.repository.ReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReadingService {
    private static final Logger logger = LoggerFactory.getLogger(ReadingService.class);
    private final ReadingRepository readingRepository;
    private final AlertService alertService;
    private final StageConfig stageConfig;

    @Autowired
    public ReadingService(ReadingRepository readingRepository, AlertService alertService, StageConfig stageConfig) {
        this.readingRepository = readingRepository;
        this.alertService = alertService;
        this.stageConfig = stageConfig;
    }

    public List<ReadingResponse> getAll() {
        return readingRepository.findAll().stream()
                .map(ReadingMapper::toReadingResponse)
                .toList();
    }

    public ReadingResponse get(Long id) {
        return ReadingMapper.toReadingResponse(
                readingRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading not found."))
        );
    }

    public ReadingResponse getLatest() {
        return readingRepository.findTopByOrderByTimestampDesc()
                .map(ReadingMapper::toReadingResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma leitura encontrada."));
    }

    public ReadingResponse save(ReadingRequest request) {
        Reading reading = ReadingMapper.toReading(request, stageConfig.getCurrentStage());
        reading = readingRepository.save(reading);
        alertService.checkThreshold(reading);
        logger.info("Reading successfully saved. Stage: {}", stageConfig.getCurrentStage());
        return ReadingMapper.toReadingResponse(reading);
    }
}