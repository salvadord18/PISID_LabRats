package MongoDB;

import MongoDB.entities.Experiencia;
import MongoDB.mappers.ExperienciaMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MainMongoDB {

    public static void main(String[] args) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream("cloudToMongo.ini"));
        var mongo_address = p.getProperty("mongo_address");
        var mongo_user = p.getProperty("mongo_user");
        var mongo_password = p.getProperty("mongo_password");
        var mongo_replica = p.getProperty("mongo_replica");
        var mongo_database = p.getProperty("mongo_database");
        var mongo_authentication = p.getProperty("mongo_authentication");

        String mongoURI = "mongodb://";
        if (mongo_authentication.equals("true")) mongoURI = mongoURI + mongo_user + ":" + mongo_password + "@";
        mongoURI = mongoURI + mongo_address;
        if (!mongo_replica.equals("false"))
            if (mongo_authentication.equals("true"))
                mongoURI = mongoURI + "/?replicaSet=" + mongo_replica + "&authSource=admin";
            else mongoURI = mongoURI + "/?replicaSet=" + mongo_replica;
        else if (mongo_authentication.equals("true")) mongoURI = mongoURI + "/?authSource=admin";

        MongoClientURI bru = (new MongoClientURI(mongoURI));


        String endpoint = "database-1.chugk2cki1rp.us-east-1.rds.amazonaws.com"; // seu endpoint RDS
        String databaseName = "Pisid25"; // o nome da sua base de dados específica
        String userName = "Pisid25"; // seu usuário de banco de dados
        String password = "Pisid252024."; // sua senha de banco de dados
        String port = "3306"; // a porta padrão do MariaDB

        String jdbcUrl = "jdbc:mariadb://" + endpoint + ":" + port + "/" + databaseName + "?useSSL=false"; // String de conexão JDBC

        try (var mysqlDb = DriverManager.getConnection(jdbcUrl, userName, password);
             var mongoClient = new MongoClient(bru)) {
            var s = mysqlDb.createStatement();

             //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
            var result = s.executeQuery("select * from experiencia;");
            var experiencias = ExperienciaMapper.mapList(result);
            for(Experiencia experiencia : experiencias){
                System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
            }
            var mongoDb = mongoClient.getDB(mongo_database);
            var threadFetchMongoToSql = ProcessarTemperatura.builder()
                    .mongoDb(mongoDb)
                    .sqlDb(mysqlDb)
                    .build();

//            threadFetchMongoToSql.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
