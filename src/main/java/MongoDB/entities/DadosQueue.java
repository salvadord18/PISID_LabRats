package MongoDB.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DadosQueue {
    ArrayList<DadosTemperaturaMongoDB> dadosTemperaturaMongoDB = new ArrayList<>();
    ArrayList<DadosPortasMongoDB> dadosPortasMongoDB = new ArrayList<>();
    ArrayList<DadosTemperaturaMongoDB> dadosTratadosTemperaturas = new ArrayList<>();
    ArrayList<DadosPortasMongoDB> dadosTratadosPortas = new ArrayList<>();

    private DadosQueue() {
    }

    public synchronized void pushPortasTratadas(Collection<DadosPortasMongoDB> data) {
        dadosTratadosPortas.addAll(data);
        notifyAll();
    }

    public synchronized DadosPortasMongoDB popPortasTratadas() throws InterruptedException {
        if (dadosTratadosPortas.size() > 0) {
            return dadosTratadosPortas.remove(0);
        } else {
            wait();
        }
        return popPortasTratadas();
    }

    public synchronized void pushData(DadosTemperaturaMongoDB data) {
        dadosTemperaturaMongoDB.add(data);
        notifyAll();
    }

    public synchronized DadosTemperaturaMongoDB popData() throws InterruptedException {
        if (dadosTemperaturaMongoDB.size() > 0) {
            return dadosTemperaturaMongoDB.remove(0);
        } else {
            wait();
        }
        return popData();
    }

    public synchronized void pushPortasMongo(Collection<DadosPortasMongoDB> data) {
        dadosPortasMongoDB.addAll(data);
        notifyAll();
    }

    public synchronized DadosPortasMongoDB popPortasMongo() throws InterruptedException {
        if (dadosPortasMongoDB.size() > 0) {
            return dadosPortasMongoDB.remove(0);
        } else {
            wait();
        }
        return popPortasMongo();
    }

    public synchronized void pushTempsTratadas(Collection<DadosTemperaturaMongoDB> data) {
        dadosTratadosTemperaturas.addAll(data);
        notifyAll();
    }

    public synchronized DadosTemperaturaMongoDB popTempsTratadas() throws InterruptedException {
        if (dadosTratadosTemperaturas.size() > 0) {
            return dadosTratadosTemperaturas.remove(0);
        } else {
            wait();
        }
        return popTempsTratadas();
    }

    public synchronized ArrayList<DadosTemperaturaMongoDB> getDadosTratadosTemperaturas() {
        return dadosTratadosTemperaturas;
    }

    public synchronized ArrayList<DadosPortasMongoDB> getDadosTratadosPortas() {
        return dadosTratadosPortas;
    }

    private static DadosQueue single_instance = null;

    public static synchronized DadosQueue getInstance() {
        if (single_instance == null)
            single_instance = new DadosQueue();
        return single_instance;
    }
}
