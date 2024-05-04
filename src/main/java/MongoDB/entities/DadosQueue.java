package MongoDB.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DadosQueue {
    ArrayList<DadosTemperaturaMongoDB> dadosTemperaturaMongoDB = new ArrayList<>();
    ArrayList<DadosPortasMongoDB> dadosPortasMongoDB = new ArrayList<>();
    ArrayList<Experiencia> experiencias = new ArrayList<>();

    private DadosQueue() {
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
    public synchronized void pushExperiencia(Experiencia data) {
        experiencias.add(data);
        notifyAll();
    }

    public synchronized Experiencia popExperiencia() {
        if (experiencias.size() > 0) {
            return experiencias.remove(0);
        } else {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        return popExperiencia();
    }

    private static DadosQueue single_instance = null;

    public static synchronized DadosQueue getInstance() {
        if (single_instance == null)
            single_instance = new DadosQueue();
        return single_instance;
    }
}
