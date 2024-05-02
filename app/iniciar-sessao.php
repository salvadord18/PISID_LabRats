<?php
session_start();
include 'db.php'; // Assegura que este ficheiro contém a ligação à base de dados

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = $_POST['username'];
    $password = $_POST['password'];

    // Chama o procedimento armazenado
    $stmt = $conn->prepare("CALL IniciarSessao(?, ?)");
    $stmt->bind_param("ss", $username, $password);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    if ($row['IsValid']) {
        // Se IsValid for 1, utilizador e palavra-passe estão corretos
        $_SESSION['username'] = $username; // Armazenar o nome de utilizador na sessão
        header("Location: /labrats/app/inicio.html"); // Redireciona para a página inicial
        exit();
    } else {
        echo "Nome de utilizador ou palavra-passe incorretos.";
    }
    $stmt->close();
}
$conn->close();
?>
