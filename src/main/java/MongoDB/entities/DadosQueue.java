package MongoDB.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DadosQueue {
    ArrayList<DadosTemperaturaMongoDB> dadosTemperaturaMongoDB = new ArrayList<>();
    ArrayList<DadosTemperaturaMongoDB> dadosTratadosTemperaturas = new ArrayList<>();
    ArrayList<DadosPortasMongoDB> dadosPortasMongoDB = new ArrayList<>();
    ArrayList<DadosPortasMongoDB> dadosTratadosPortas = new ArrayList<>();

    private DadosQueue() {
    }

    public synchronized void pushPortasTratadas(Collection<DadosPortasMongoDB> data) {
        dadosTratadosPortas.addAll(data);
        notifyAll();
    }

    public synchronized DadosPortasMongoDB popPortasTratadas() {
        if (dadosTratadosPortas.size() > 0) {
            return dadosTratadosPortas.remove(0);
        } else {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        return popPortasTratadas();
    }
    public synchronized void pushData(Collection<DadosTemperaturaMongoDB> data) {
        dadosTemperaturaMongoDB.addAll(data);
        notifyAll();
    }

    public synchronized DadosTemperaturaMongoDB popData() {
        if (dadosTemperaturaMongoDB.size() > 0) {
            return dadosTemperaturaMongoDB.remove(0);
        } else {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        return popData();
    }
    public synchronized void pushPortasMongo(Collection<DadosPortasMongoDB> data) {
        dadosPortasMongoDB.addAll(data);
        notifyAll();
    }

    public synchronized DadosPortasMongoDB popPortasMongo() {
        if (dadosPortasMongoDB.size() > 0) {
            return dadosPortasMongoDB.remove(0);
        } else {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        return popPortasMongo();
    }

    public synchronized void pushTempsTratadas(Collection<DadosTemperaturaMongoDB> data) {
        dadosTratadosTemperaturas.addAll(data);
        notifyAll();
    }

    public synchronized DadosTemperaturaMongoDB popTempsTratadas() {
        if (dadosTratadosTemperaturas.size() > 0) {
            return dadosTratadosTemperaturas.remove(0);
        } else {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        return popTempsTratadas();
    }

    public synchronized ArrayList<DadosTemperaturaMongoDB> getDadosTemperaturaMongoDB(){
        return dadosTemperaturaMongoDB;
    }

    public synchronized ArrayList<DadosPortasMongoDB> getDadosPortasMongoDB(){
        return dadosPortasMongoDB;
    }

    private static DadosQueue single_instance = null;

    public static synchronized DadosQueue getInstance() {
        if (single_instance == null)
            single_instance = new DadosQueue();
        return single_instance;
    }
}
