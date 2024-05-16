package MongoDB;

import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ResetMongoDB extends Thread{

    private final Connection sqlDb;
    private final DB mongoDB;
    private Date firstTime;
    private Date thisTime;


    @Override
    public void run() {
        firstTime = new Date();
        while(true) {
            thisTime = new Date();
            if (thisTime.getTime() - firstTime.getTime() >= TimeUnit.DAYS.toMillis(7)) {
                //Perform validation before executing the task
                try {
                    validaExperiencia();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(TimeUnit.DAYS.toMillis(7));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void validaExperiencia() throws SQLException, InterruptedException {
        String tempIdeal = "{ call GetExisteExperienciaPendente(?) }";
        CallableStatement calls = this.sqlDb.prepareCall(tempIdeal);
        calls.execute();
        int resultado = calls.getInt(1);
        if(resultado == 1){

            Thread.sleep(30*60*1000);
            validaExperiencia();
        }
        if (resultado == 0){
            dropMongo();
        }
    }


    public void dropMongo(){
        this.mongoDB.getCollection("Sensor_Porta").drop();
        this.mongoDB.getCollection("Sensor_Temperatura").drop();
        System.out.println("Dados Apagados no Mongo");
        firstTime = thisTime;
    }





}