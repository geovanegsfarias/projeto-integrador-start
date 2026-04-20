package br.senac.sp.projeto_integrador.controller;

import br.senac.sp.projeto_integrador.configuration.StageConfig;
import br.senac.sp.projeto_integrador.dto.request.StageRequest;
import br.senac.sp.projeto_integrador.model.BeerStage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stage")
public class StageController {

    private final StageConfig stageConfig;

    @Autowired
    public StageController(StageConfig stageConfig) {
        this.stageConfig = stageConfig;
    }

    @GetMapping
    public ResponseEntity<BeerStage> getCurrentStage() {
        return ResponseEntity.ok(stageConfig.getCurrentStage());
    }

    @PutMapping
    public ResponseEntity<BeerStage> setCurrentStage(@Valid @RequestBody StageRequest request) {
        stageConfig.setCurrentStage(request.stage());
        return ResponseEntity.ok(stageConfig.getCurrentStage());
    }
}