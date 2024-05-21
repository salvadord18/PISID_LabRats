package SQLCloud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.Types;


public class Main {
    public static void main(String[] args) {

        //   String IP_ADDRESS = "194.210.86.10";
        //   String DATABASE_NAME = "pisid2024";
        //  String USERNAME = "aluno";
        //  String PASSWORD = "aluno";
        //  String PORT = "3306";

        String IP_ADDRESS = "127.0.0.1";
        String DATABASE_NAME = "pisid2024";
        String USERNAME = "root";
        String PASSWORD = "123";
        String PORT = "3306";

         String JDBC_URL = "jdbc:mariadb://" + IP_ADDRESS + ":" + PORT + "/" + DATABASE_NAME + "?useSSL=false"; // String de conexão JDBC

        String IP_ADDRESS1 = "127.0.0.1";
        String DATABASE_NAME1 = "pisid25";
        String USERNAME1 = "root";
        String PASSWORD1 = "123";
        String PORT1 = "3306";

        String JDBC_URL1 = "jdbc:mariadb://" + IP_ADDRESS1 + ":" + PORT1 + "/" + DATABASE_NAME1 + "?useSSL=false"; // String de conexão JDBC


        while (true) {
            // Verifica se há uma experiência em andamento
            int existeExperiencia = GetExisteExperienciaADecorrer(JDBC_URL1, USERNAME1, PASSWORD1);

            if (existeExperiencia == 1) {
                // Obtém o número da experiência em processamento

                int experiencia = GetExperienciaEmProcessamento(JDBC_URL1, USERNAME1, PASSWORD1);
                boolean hasCorredores = GetCorredoresExperiencia(JDBC_URL1, USERNAME1, PASSWORD1, experiencia);

                if (!hasCorredores) {
                    // Insere corredores com indicação da experiência
                    List<Corredor> corredoresToSend = CorredorMapper.fetchCorredores(JDBC_URL, USERNAME, PASSWORD);
                    DatabaseTransfer.sendCorredores(corredoresToSend, JDBC_URL1, USERNAME1, PASSWORD1, experiencia);
                    System.out.println("Corredores enviados para a experiência " + experiencia);
                } else {
                    System.out.println("Corredores já existem para a experiência " + experiencia);
                }
            } else {
                // Obtém o número de salas
                int numeroDeSalas = ConfiguracaoLabirintoMapper.fetchNumeroDeSalas(JDBC_URL, USERNAME, PASSWORD);
                // Chama o procedimento armazenado para criar ou editar salas
                DatabaseTransfer.sendNumeroDeSalas(numeroDeSalas, JDBC_URL1, USERNAME1, PASSWORD1);
                System.out.println("Número de salas enviado.");
            }


            // Aguarda um tempo antes de verificar novamente (por exemplo, 10 segundos)
            try {
                Thread.sleep(10000); // 10000 milissegundos = 10 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int GetExisteExperienciaADecorrer(String jdbcUrl, String username, String password) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetExisteExperienciaADecorrer(?)}")) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int GetExperienciaEmProcessamento(String jdbcUrl, String username, String password) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetExperienciaEmProcessamento(?)}")) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean GetCorredoresExperiencia(String jdbcUrl, String username, String password, int experienciaID) {
        String sql = "{CALL GetCorredoresExperiencia(?, ?)}";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, experienciaID);
            stmt.registerOutParameter(2, java.sql.Types.INTEGER);

            stmt.execute();
            int hasCorredores = stmt.getInt(2);
            return hasCorredores == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}





