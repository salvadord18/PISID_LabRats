<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$userId = $_SESSION['user_id'] ?? null;
$experienciaId = $_GET['Experiencia_ID'] ?? null;

if (!$userId || !$experienciaId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Mudar o estado da experiência para 'A aguardar'
$estadoAguardar = 1; // Asumindo que o ID do estado 'A aguardar' é 1
$updateEstadoStmt = $conn->prepare("UPDATE estadoexperiencia SET Estado_Estado_ID = ? WHERE Experiencia_Experiencia_ID = ?");
$updateEstadoStmt->bind_param("ii", $estadoAguardar, $experienciaId);
$updateEstadoStmt->execute();

if ($updateEstadoStmt->affected_rows > 0) {
    // Redirecionar para a página de visualização com uma mensagem de sucesso
    header("Location: /labrats/app/inicio.php");
} else {
    // Redirecionar com uma mensagem de erro
    header("Location: /labrats/app/inicio.php");
}
$updateEstadoStmt->close();
$conn->close();
?>