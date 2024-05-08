<?php
session_start();
include 'db.php';

$username = $_POST['username'];
$password = $_POST['password'];

$stmt = $conn->prepare("CALL IniciarSessao(?, ?, @Valid, @UserID)");
$stmt->bind_param("ss", $username, $password);
$stmt->execute();

// Recupera os valores dos parâmetros de saída
$result = $conn->query("SELECT @Valid AS Valid, @UserID AS UserID");
$row = $result->fetch_assoc();

if ($row['Valid']) {
    $_SESSION['user_id'] = $row['UserID'];
    $_SESSION['username'] = $username;
    header("Location: /labrats/app/inicio.php");
    exit();
} else {
    echo "<script>alert('Nome de utilizador ou palavra-passe incorretos.'); window.history.back();</script>";
}

$conn->close();
?>