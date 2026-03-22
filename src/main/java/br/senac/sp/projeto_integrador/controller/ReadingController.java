package br.senac.sp.projeto_integrador.controller;

import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import br.senac.sp.projeto_integrador.dto.response.ReadingResponse;
import br.senac.sp.projeto_integrador.service.ReadingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {
    private final ReadingService readingService;

    @Autowired
    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @GetMapping
    public ResponseEntity<List<ReadingResponse>> getAllReading() {
        return ResponseEntity.ok(readingService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadingResponse> getReading(@PathVariable Long id) {
        return ResponseEntity.ok(readingService.get(id));
    }

    @GetMapping("/latest")
    public ResponseEntity<ReadingResponse> getLatest() {
        return ResponseEntity.ok(readingService.getLatest());
    }

    @PostMapping
    public ResponseEntity<ReadingResponse> saveReading(@Valid @RequestBody ReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readingService.save(request));
    }

}