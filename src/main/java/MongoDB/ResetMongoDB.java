package MongoDB;

import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@RequiredArgsConstructor
public class ResetMongoDB extends Thread{

    private final DB mongoDB;
    private Connection sqlDb;


    @Override
    public void run() {

        //validaExperiencia();

    }

    public void validaExperiencia() throws SQLException, InterruptedException {
        String tempIdeal = "{ call GetExisteExperienciaPendente(?) }";
        CallableStatement calls = this.sqlDb.prepareCall(tempIdeal);
        calls.execute();
        int resultado = calls.getInt(1);
        if(resultado == 0){

            Thread.sleep(30000);
            validaExperiencia();
        }
        if (resultado == 1){
            dropMongo();
        }
    }


    public void dropMongo(){
        this.mongoDB.getCollection("Sensor_Porta").drop();
        this.mongoDB.getCollection("Sensor_Temperatura").drop();
        System.out.println("Dados Apagados no Mongo");
    }



}