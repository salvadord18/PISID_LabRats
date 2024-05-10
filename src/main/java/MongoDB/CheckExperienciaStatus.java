package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.enums.ExperienciaStatus;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;

//Thread que vai verificar o estado da experiencia e atualiza-lo
// Por fazer, o código ainda é apenas para teste
@RequiredArgsConstructor
public class CheckExperienciaStatus extends Thread{
    private final Connection sqlDb;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
        }

        // Aqui vai chamar o SP para retornar o estado da experiencia, se for Terminado
        //callSpCheckStatus() exemplo ----- por fazer ainda
        // faz set do estado da experiencia como está abaixo
//        CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.TERMINADA);
    }
}
