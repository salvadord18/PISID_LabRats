package SQLCloud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfiguracaoLabirintoMapper {

    private static Integer numeroDeSalas = null; // Armazenar o número de salas

    // Método para buscar o número de salas (com cache)
    public static int fetchNumeroDeSalas(String url, String user, String password) {
        if (numeroDeSalas != null) {
            return numeroDeSalas;
        }

        // Consulta SQL para buscar o número de salas
        String query = "SELECT numerodesalas FROM configuracaolabirinto LIMIT 1"; // Assume que há apenas um registro

        // Tente com recursos para gerenciar a conexão e o statement
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Verificar se há resultado e obter o valor
            if (resultSet.next()) {
                numeroDeSalas = resultSet.getInt("numerodesalas");
            } else {
                // Considerar um valor padrão ou lançar uma exceção se esperado sempre um resultado
                numeroDeSalas = 0; // Valor padrão
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar número de salas", e);
        }

        return numeroDeSalas;
    }
}
