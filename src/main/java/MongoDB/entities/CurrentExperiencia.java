package MongoDB.entities;

import MongoDB.entities.enums.ExperienciaStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

// Class para a experiencia que está a decorrer
public class CurrentExperiencia {

   Experiencia experiencia;
    ExperienciaStatus estadoExperiencia;

    private CurrentExperiencia() {
    }

    public synchronized  void setEstadoExperiencia(ExperienciaStatus estadoExperiencia){
        this.estadoExperiencia = estadoExperiencia;
        notifyAll();
    }

    public synchronized  Experiencia getIfEstadoEquals(ExperienciaStatus estadoExperiencia){
        if (!Objects.equals(this.estadoExperiencia, estadoExperiencia)){
            try {
                wait();
            } catch (InterruptedException e) {
                getIfEstadoEquals(estadoExperiencia);
            }
        }
        return experiencia;
    }

    public synchronized Experiencia getExperiencia(){
        if(Objects.isNull(experiencia)){
            try {
                wait();
            } catch (InterruptedException e) {
                return  getExperiencia();
            }
        }
        return experiencia;
    }

    public boolean isEstado(ExperienciaStatus estadoExperiencia){
        return estadoExperiencia.equals(this.estadoExperiencia);
    }

    public synchronized void setExperiencia(Experiencia experiencia) {
        this.experiencia = experiencia;
        notifyAll();
    }

    private static CurrentExperiencia single_instance = null;

    public static synchronized CurrentExperiencia getInstance() {
        if (single_instance == null)
            single_instance = new CurrentExperiencia();
        return single_instance;
    }
}
