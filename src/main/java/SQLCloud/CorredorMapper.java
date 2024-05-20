package SQLCloud;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CorredorMapper {

    // Método para buscar corredores no banco de dados
    public static List<Corredor> fetchCorredores(String url, String user, String password) {
        List<Corredor> corredores = new ArrayList<>();

        // Definindo a query SQL, usando alias para as colunas conforme necessário
        String query = "SELECT salaa AS salaOrigem, salab AS salaDestino FROM corredor";

        // Tente com recursos para gerenciar a conexão, statement e resultSet
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Processa cada linha do resultado
            while (resultSet.next()) {
                Corredor corredor = new Corredor();
                corredor.setSalaOrigem(resultSet.getString("salaOrigem"));
                corredor.setSalaDestino(resultSet.getString("salaDestino"));
                corredores.add(corredor);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }

        return corredores;
    }
}
