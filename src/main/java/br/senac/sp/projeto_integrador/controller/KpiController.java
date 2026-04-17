package br.senac.sp.projeto_integrador.controller;

import br.senac.sp.projeto_integrador.dto.response.*;
import br.senac.sp.projeto_integrador.service.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final KpiService kpiService;

    @Autowired
    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @GetMapping("/desvio-padrao")
    public ResponseEntity<DesvioPadraoKpiResponse> getDesvioPadrao() {
        return ResponseEntity.ok(kpiService.getDesvioPadrao());
    }

    @GetMapping("/conformidade")
    public ResponseEntity<ConformidadeKpiResponse> getConformidade() {
        return ResponseEntity.ok(kpiService.getConformidade());
    }

    @GetMapping("/energia")
    public ResponseEntity<EnergiaKpiResponse> getEnergia() {
        return ResponseEntity.ok(kpiService.getEnergia());
    }

    @GetMapping("/temperatura/historico")
    public ResponseEntity<List<TemperaturaAgregadaResponse>> getHistoricoTemperatura() {
        return ResponseEntity.ok(kpiService.getHistoricoTemperatura());
    }

    @GetMapping("/temperatura/por-etapa")
    public ResponseEntity<List<MediaPorEtapaResponse>> getMediaTemperaturaPorEtapa() {
        return ResponseEntity.ok(kpiService.getMediaTemperaturaPorEtapa());
    }

    @GetMapping("/alertas/por-severidade")
    public ResponseEntity<AlertasKpiResponse> getAlertasPorSeveridade() {
        return ResponseEntity.ok(kpiService.getAlertasPorSeveridade());
    }

    @GetMapping("/etapas/duracao")
    public ResponseEntity<List<EtapaDuracaoResponse>> getEtapasDuracao() {
        return ResponseEntity.ok(kpiService.getEtapasDuracao());
    }
}