<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_SESSION['username'])) {
    $username = $_SESSION['username']; // Usa o nome de utilizador da sessão
    $nomeUtilizador = $_POST['nome-utilizador'] ?? '';
    $email = $_POST['email'] ?? '';
    $telefone = $_POST['telefone'] ?? '';

    // Preparar chamada ao procedimento armazenado
    $stmt = $conn->prepare("CALL AtualizarPerfil(?, ?, ?, ?)");
    $stmt->bind_param("ssss", $username, $nomeUtilizador, $email, $telefone);

    if ($stmt->execute()) {
        // Obter resultado do procedimento armazenado
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        
        if ($row && $row['rows_affected'] > 0) {
            echo "<script>alert('Dados atualizados com sucesso.'); window.location.href='/labrats/app/perfil.php';</script>";
        } else {
            echo "<script>alert('Nenhuma alteração foi feita.'); window.history.back();</script>";
        }
    } else {
        echo "<script>alert('Erro ao atualizar dados: " . $stmt->error . "'); window.history.back();</script>";
    }
    $stmt->close();
    $conn->close();
} else {
    echo "<script>alert('Acesso não autorizado.'); window.location.href='/labrats/app/iniciar-sessao.php';</script>";
}
?>
