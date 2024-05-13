<?php
session_start();
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Receber o ID da experiência por POST
$experienciaId = $_POST['Experiencia_ID'] ?? null;

if (!$experienciaId) {
    echo "<script>alert('ID da experiência não fornecido.'); window.history.back();</script>";
    exit();
}

// Recebe dados do formulário
$descricao = $_POST['experience_description'] ?? '';
$numeroRatos = $_POST['experience-rats'] ?? 0;
$limiteRatosSala = $_POST['experience-rats-limit'] ?? 0;
$segundosSemMovimento = $_POST['experience-seconds'] ?? 0;
$temperaturaIdeal = $_POST['experience-temperature'] ?? 0.0;
$variacaoTemperaturaMaxima = $_POST['experience-max-temperature'] ?? 0.0;
$numeroOutliersMaximo = $_POST['experience-outliers'] ?? 0;

$updateStmt = $conn->prepare("CALL AtualizarExperiencia(?, ?, ?, ?, ?, ?, ?, ?)");
$updateStmt->bind_param("isiiiddi", $experienciaId, $descricao, $numeroRatos, $limiteRatosSala, $segundosSemMovimento, $temperaturaIdeal, $variacaoTemperaturaMaxima, $numeroOutliersMaximo);
$updateStmt->execute();

// Atualizar estado para 'A aguardar'
$estadoAguardar = 1;
$estadoStmt = $conn->prepare("CALL InserirEstadoExperiencia(?, ?)");
$estadoStmt->bind_param("ii", $experienciaId, $estadoAguardar);
$estadoStmt->execute();

if ($updateStmt->affected_rows > 0 || $estadoStmt->affected_rows > 0) {
    echo "<script>alert('Alterações guardadas com sucesso e estado atualizado para A aguardar.'); window.location.href='/labrats/app/visualizar-experiencia.php?Experiencia_ID=$experienciaId';</script>";
} else {
    echo "<script>alert('Erro ao guardar as alterações ou atualizar o estado.'); window.history.back();</script>";
}

$updateStmt->close();
$estadoStmt->close();
$conn->close();
