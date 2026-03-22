package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import br.senac.sp.projeto_integrador.dto.response.ReadingResponse;
import br.senac.sp.projeto_integrador.mapper.ReadingMapper;
import br.senac.sp.projeto_integrador.repository.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingService {
    private final ReadingRepository readingRepository;

    @Autowired
    public ReadingService(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
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
        return ReadingMapper.toReadingResponse(readingRepository.save(ReadingMapper.toReading(request)));
    }

}