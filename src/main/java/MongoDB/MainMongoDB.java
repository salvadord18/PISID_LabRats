package MongoDB;

import MongoDB.entities.Experiencia;
import MongoDB.mappers.ExperienciaMapper;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.Properties;

public class MainMongoDB {

    ConnectToMongo connectMongo;
    MongoClient myConnectionToMongo;
    ConnectToSQL connectToSQL;
    static Statement s;

    public static void IniciarExperiencia(ConnectToSQL connectToSQL) throws InterruptedException, SQLException {
        while (true) {
            String testeCallSP = "{CALL Get_ProximaExperiencia(?)}";
            CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            int resultado = cs.getInt(1);

            if (resultado != -1){
                System.out.println("Este é o meu resultado: " + resultado);
                break;
            }
            Thread.sleep(5000);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        //Esta parte foi toda para dentro do ConnectToMongo


        ConnectToMongo connectMongo  = new ConnectToMongo();
        MongoClient myConnectionToMongo = connectMongo.getConnectionMongo();
        ConnectToSQL connectToSQL = new ConnectToSQL();

        var s = connectToSQL.getConnectionSQL().createStatement();

        IniciarExperiencia(connectToSQL);
        //

        //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
        var result = s.executeQuery("select * from experiencia;");
        var experiencias = ExperienciaMapper.mapList(result);
        for (Experiencia experiencia : experiencias) {
            System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
        }


        var mongoDb = connectMongo.getDataBase();

        var fetchTempsMongo = new ProcessarTemperatura(mongoDb);
        var fetchDoorsMongo = new ProcessarPortas(mongoDb);
        var threadFetchToSql = new EnviarDadosMysql(connectToSQL.getConnectionSQL());


        fetchTempsMongo.start();
        fetchDoorsMongo.start();
        threadFetchToSql.start();


        fetchTempsMongo.join();
        fetchDoorsMongo.join();
        threadFetchToSql.join();

    }


}
