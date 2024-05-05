package MongoDB;

import MongoDB.entities.Corredor;
import MongoDB.entities.Experiencia;
import MongoDB.mappers.CorredorMapper;
import MongoDB.mappers.ExperienciaMapper;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;

public class MainMongoDB {

    ConnectToMongo connectMongo;
    MongoClient myConnectionToMongo;
    ConnectToSQL connectToSQL;
    static Statement s;

    public static int IniciarExperiencia(ConnectToSQL connectToSQL) throws InterruptedException, SQLException {
        while (true) {
            String testeCallSP = "{CALL Get_ProximaExperiencia(?)}";
            CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            int resultado = cs.getInt(1);
            if (resultado != -1){
                return resultado;
            }
            System.out.println("Não ha threads a aguardar");
            Thread.sleep(5000);
        }
    }

    public static void getCorredoresCurrentExperiencia(ConnectToSQL connectToSQL, int idExperiencia) throws SQLException {
        String procedureCall = "{CALL GetCorredores(?)}";
        CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(procedureCall);
        cs.setInt(1, idExperiencia);
        ResultSet resultado =  cs.executeQuery();
        while(resultado.next()) {

            System.out.println(resultado.getString(1));
            //tratar json e popular corredores

        }
        LocalDate currentDate = LocalDate.now();

        String currentDateString = currentDate.toString();
        Corredor[] corredores = {};


    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        //Esta parte foi toda para dentro do ConnectToMongo


        ConnectToMongo connectMongo  = new ConnectToMongo();
        MongoClient myConnectionToMongo = connectMongo.getConnectionMongo();
        ConnectToSQL connectToSQL = new ConnectToSQL();
        var mongoDb = connectMongo.getDataBase();

        var s = connectToSQL.getConnectionSQL().createStatement();

        //Recebe id da experiencia que está a aguardar à mais tempo
        int id = IniciarExperiencia(connectToSQL);
        System.out.println(id);
        //Get corredores da experiencia
        getCorredoresCurrentExperiencia(connectToSQL, id);






//        //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
//        var result = s.executeQuery("select * from experiencia;");
//        var experiencias = ExperienciaMapper.mapList(result);
//        for (Experiencia experiencia : experiencias) {
//            System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
//        }




        var fetchTempsMongo = new ProcessarTemperatura(mongoDb);
        var fetchDoorsMongo = new ProcessarPortas(mongoDb);
        var threadFetchToSql = new EnviarDadosMysql(connectToSQL.getConnectionSQL());
        var threadDealWithData = new TratarDados(connectToSQL.getConnectionSQL(), mongoDb);


        fetchTempsMongo.start();
        fetchDoorsMongo.start();
        threadDealWithData.start();
        threadFetchToSql.start();



        fetchTempsMongo.join();
        fetchDoorsMongo.join();
        threadDealWithData.join();
        threadFetchToSql.join();


    }


}
