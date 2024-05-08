<?php
session_start();
include 'db.php';

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_SESSION['user_id'])) {
    $userId = $_SESSION['user_id'];
    $nomeUtilizador = $_POST['nome-utilizador'];
    $email = $_POST['email'];
    $telefone = $_POST['telefone'];

    $stmt = $conn->prepare("CALL GuardarAlteracoesPerfil(?, ?, ?)");
    $stmt->bind_param("ssi", $email, $telefone, $userId);
    if ($stmt->execute()) {
        echo "<script>alert('Alterações guardadas com sucesso!'); window.location.href='/labrats/app/perfil.php';</script>";
    } else {
        echo "<script>alert('Erro ao guardar as alterações.'); window.history.back();</script>";
    }
    $stmt->close();
    $conn->close();
    exit();
} else {
    echo "Acesso não autorizado.";
}
