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