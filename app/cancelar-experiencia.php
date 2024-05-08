<?php
session_start();
include 'db.php';

if (!isset($_SESSION['user_id'])) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

$experienciaId = $_POST['experienciaId'] ?? null;
$action = $_POST['action'] ?? '';

if (!$experienciaId) {
    echo "<script>alert('Dados inválidos.'); window.history.back();</script>";
    exit();
}

$stmt = $conn->prepare("CALL CancelarExperiencia(?)");
$stmt->bind_param("i", $experienciaId);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    header("Location: /labrats/app/visualizar-experiencia.php?Experiencia_ID=$experienciaId");
    exit();
} else {
    echo "<script>alert('Erro ao atualizar o estado ou a experiência já foi cancelada.'); window.history.back();</script>";
}

$stmt->close();
$conn->close();
?>