package MongoDB;

import MongoDB.entities.Experiencia;
import MongoDB.mappers.ExperienciaMapper;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

public class MainMongoDB {

    public void IniciarExperiencia() throws InterruptedException {
        while (true) {

            Thread.sleep(5000);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        //Esta parte foi toda para dentro do ConnectToMongo

        ConnectToMongo connectMongo = new ConnectToMongo();
        MongoClient myConnectionToMongo = connectMongo.getConnectionMongo();
        ConnectToSQL connectToSQL = new ConnectToSQL();
        connectToSQL.getConnectionSQL().createStatement();


        var s = connectToSQL.getConnectionSQL().createStatement();


        //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
        var result = s.executeQuery("select * from experiencia;");
        var experiencias = ExperienciaMapper.mapList(result);
        for (Experiencia experiencia : experiencias) {
            System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
        }


        var mongoDb = connectMongo.getDataBase();

        var fetchMongo = new ProcessarTemperatura(mongoDb);
        var threadFetchToSql = new EnviarDadosMysql(connectToSQL.getConnectionSQL());


        fetchMongo.start();
        threadFetchToSql.start();

        fetchMongo.join();
        threadFetchToSql.join();


    }


}
