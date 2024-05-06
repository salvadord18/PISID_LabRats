package MongoDB.entities.enums;

public enum ExperienciaStatus {
    TERMINADA("Terminada"), EM_CURSO("Em curso"), EM_PROCESSAMENTO("Em processamento");

    String label;

    ExperienciaStatus(String label) {
        this.label = label;
    }


}
