package SQLCloud;

import java.sql.*;
import java.util.List;

public class DatabaseTransfer {
    public static void sendNumeroDeSalas(int numeroDeSalas, String url, String user, String password) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             CallableStatement callableStatement = connection.prepareCall("{CALL CriarEditarSalas(?)}")) {

            callableStatement.setInt(1, numeroDeSalas);
            callableStatement.execute();

        } catch (SQLException e) {
            System.out.println("Erro ao executar o procedimento armazenado para número de salas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendCorredores(List<Corredor> corredores, String url, String user, String password, int experiencia) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            for (Corredor corredor : corredores) {
                try (CallableStatement callableStatement = connection.prepareCall("{CALL CriarCorredor(?, ?, ?)}")) {
                    callableStatement.setInt(1, experiencia);
                    callableStatement.setString(2, corredor.getSalaOrigem());
                    callableStatement.setString(3, corredor.getSalaDestino());
                    callableStatement.execute();
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar o procedimento armazenado para corredores: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
