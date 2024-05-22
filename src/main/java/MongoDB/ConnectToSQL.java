package MongoDB;

import lombok.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public class ConnectToSQL {

    Connection mysqlDB;
    //Passar para dentro do Ini
//    String endpoint = "database-1.chugk2cki1rp.us-east-1.rds.amazonaws.com"; // seu endpoint RDS
    //    String endpoint = "192.168.110.79";
    String endpoint = "127.0.0.1";
    @Getter
    String databaseName = "pisid26";
    @Getter
    String userName = "root";
    @Getter
    String password = "";
    //    @Getter
//    String databaseName = "Pisid25"; // o nome da sua base de dados específica
//    @Getter
//    String userName = "Pisid25"; // seu usuário de banco de dados
    //    @Getter
//    String password = "Pisid252024"; // sua senha de banco de dados
//    @Getter
//    String password = "Pisid252024."; // sua senha de banco de dados
    String port = "3306"; // a porta padrão do MariaDB
    String jdbcUrl = "jdbc:mariadb://" + endpoint + ":" + port + "/" + databaseName + "?useSSL=false"; // String de conexão JDBC


    public ConnectToSQL() throws SQLException {
        this.mysqlDB = DriverManager.getConnection(jdbcUrl, userName, password);
    }

    public static Supplier<ConnectToSQL> getSuplier(){
       return () -> {
            try {
                return new ConnectToSQL();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public Connection getConnectionSQL() {
        return mysqlDB;
    }
}
