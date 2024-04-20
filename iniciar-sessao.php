<?php
session_start();
include 'db.php'; // Asegura que este ficheiro contém a conexão com a base de dados

// Verifica se os dados foram submetidos
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = $_POST['email'];
    $password = $_POST['password']; // Vamos assumir que este é o campo 'telefone' para teste

    // Prepara uma consulta para evitar injeções SQL
    $stmt = $conn->prepare("SELECT Utilizador_ID, TelefoneUtilizador FROM utilizador WHERE EmailUtilizador = ? AND EstadoRegisto = 'A'");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();

        // Imprime os valores para debug
        echo "Password recebida: " . $password . "<br>";
        echo "Telefone esperado: " . $user['TelefoneUtilizador'] . "<br>";

        // Verifica se o 'telefone' corresponde
        if ($password === (string)$user['TelefoneUtilizador']) {
            // A palavra-passe está correta, então inicia a sessão
            $_SESSION['user_id'] = $user['Utilizador_ID'];
            $_SESSION['email'] = $email;  // Armazenar o email na sessão pode ser útil
            header("Location: inicio.html"); // Redireciona para a página inicial
            exit();
        } else {
            echo "Número de telefone inválido como password.";
        }
    } else {
        echo "Nenhum utilizador encontrado com esse e-mail ou o registo não está ativo.";
    }
    $stmt->close();
}
$conn->close();
?>
