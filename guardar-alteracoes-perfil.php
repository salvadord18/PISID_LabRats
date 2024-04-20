<?php
session_start();
include 'db.php';

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_SESSION['user_id'])) {
    $userId = $_SESSION['user_id'];
    $nomeUtilizador = $_POST['nome-utilizador'];
    $email = $_POST['email'];
    $telefone = $_POST['telefone'];

    $query = "UPDATE utilizador SET NomeUtilizador = ?, EmailUtilizador = ?, TelefoneUtilizador = ? WHERE Utilizador_ID = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("sssi", $nomeUtilizador, $email, $telefone, $userId);
    if ($stmt->execute()) {
        echo "Dados atualizados com sucesso.";
    } else {
        echo "Erro ao atualizar dados: " . $stmt->error;
    }
    $stmt->close();
    $conn->close();
    header("Location: perfil.php"); // Redireciona de volta para a página de perfil
    exit();
} else {
    echo "Acesso não autorizado.";
}
?>
