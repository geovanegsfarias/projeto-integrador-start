package br.senac.sp.projeto_integrador.configuration;

import br.senac.sp.projeto_integrador.model.BeerStage;
import org.springframework.stereotype.Component;

@Component
public class StageConfig {

    private BeerStage currentStage = BeerStage.MASHING;

    public BeerStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(BeerStage stage) {
        this.currentStage = stage;
    }
}