package br.senac.sp.projeto_integrador.model;

public enum BeerStage {
    MASHING("Mashing"),
    BOILING("Boiling"),
    FERMENTATION("Fermentation"),
    MATURATION("Maturation");

    private String stage;

    BeerStage(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }

}