<?php
session_start();
include 'db.php';

if (!isset($_SESSION['user_id'])) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

$experienciaId = $_POST['experienciaId'] ?? null;
$action = $_POST['action'] ?? '';

if (!$experienciaId || $action !== 'editar') {
    echo "<script>alert('Dados inválidos.'); window.history.back();</script>";
    exit();
}

// Atualiza o estado para 'Edicao'
$novoEstadoId = 2; // Supõe que 2 seja o ID para 'Edicao'
$stmt = $conn->prepare("CALL AlterarEstadoExperiencia(?, ?)");
$stmt->bind_param("ii", $experienciaId, $novoEstadoId);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    header("Location: /labrats/app/editar-experiencia.php?Experiencia_ID=$experienciaId");
    exit();
} else {
    echo "<script>alert('Erro ao atualizar o estado ou a experiência já está em edição.'); window.location.href='/labrats/app/visualizar-experiencia.php?Experiencia_ID=$experienciaId';</script>";
}

$stmt->close();
$conn->close();
?>