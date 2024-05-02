<?php
session_start();
include 'db.php'; // Este ficheiro deverá conter as informações de ligação à base de dados

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Apanha os dados do formulário
    $descricao = $_POST['experience_description'];
    $numeroRatos = $_POST['experience-rats'];
    $limiteRatosSala = $_POST['experience-rats-limit'];
    $segundosSemMovimento = $_POST['experience-seconds'];
    $temperaturaIdeal = $_POST['experience-temperature'];
    $variacaoTemperaturaMaxima = $_POST['experience-max-temperature'];
    $numeroOutliersMaximo = $_POST['experience-outliers'];
    $utilizadorID = $_SESSION['user_id']; // Supõe-se que o ID do utilizador está armazenado na sessão

    // Prepara a query SQL para inserção dos dados
    $query = "INSERT INTO experiencia (Utilizador_Utilizador_ID, Descricao, NumeroRatos, LimiteRatosSala, SegundosSemMovimento, TemperaturaIdeal, VariacaoTemperaturaMaxima, NumeroOutliersMaximo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($query);
    if ($stmt) {
        $stmt->bind_param("isiiiddi", $utilizadorID, $descricao, $numeroRatos, $limiteRatosSala, $segundosSemMovimento, $temperaturaIdeal, $variacaoTemperaturaMaxima, $numeroOutliersMaximo);
        $stmt->execute();
        if ($stmt->affected_rows > 0) {
            echo "<script>alert('Experiência criada com sucesso.'); window.location.href='experiencias.php';</script>";
        } else {
            echo "<script>alert('Erro ao criar a experiência: " . addslashes($stmt->error) . "'); window.history.back();</script>";
        }
        $stmt->close();
    } else {
        echo "<script>alert('Erro ao preparar a consulta: " . addslashes($conn->error) . "'); window.history.back();</script>";
    }
    $conn->close();
}
?>
