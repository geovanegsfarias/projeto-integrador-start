package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import br.senac.sp.projeto_integrador.dto.response.ReadingResponse;
import br.senac.sp.projeto_integrador.mapper.ReadingMapper;
import br.senac.sp.projeto_integrador.model.Alert;
import br.senac.sp.projeto_integrador.model.Reading;
import br.senac.sp.projeto_integrador.repository.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReadingService {
    private final ReadingRepository readingRepository;
    private final AlertService alertService;

    @Autowired
    public ReadingService(ReadingRepository readingRepository, AlertService alertService) {
        this.readingRepository = readingRepository;
        this.alertService = alertService;
    }

    public List<ReadingResponse> getAll() {
        return readingRepository.findAll().stream().map(reading -> ReadingMapper.toReadingResponse(reading)).toList();
    }

    public ReadingResponse get(Long id) {
        return ReadingMapper.toReadingResponse(readingRepository.findById(id).orElseThrow(() -> new RuntimeException("Reading not found.")));
    }

    public ReadingResponse getLatest() {
        return ReadingMapper.toReadingResponse(readingRepository.findTopByOrderByTimestampDesc().orElseThrow(() -> new RuntimeException("Reading not found.")));
    }

    public ReadingResponse save(ReadingRequest request) {
        Reading reading = readingRepository.save(ReadingMapper.toReading(request));
        alertService.checkThreshold(reading);
        return ReadingMapper.toReadingResponse(reading);
    }

}