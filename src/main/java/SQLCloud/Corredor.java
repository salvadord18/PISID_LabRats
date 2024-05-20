package SQLCloud;

public class Corredor {
    private String salaOrigem;
    private String salaDestino;

    public String getSalaDestinoComOrigem(String salaOrigem){
        return salaDestino;
    }

    public String getSalaOrigem() {
        return salaOrigem;
    }

    public void setSalaOrigem(String salaOrigem) {
        this.salaOrigem = salaOrigem;
    }

    public String getSalaDestino() {
        return salaDestino;
    }

    public void setSalaDestino(String salaDestino) {
        this.salaDestino = salaDestino;
    }
}

