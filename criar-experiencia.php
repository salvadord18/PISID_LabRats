<?php
session_start();
include 'db.php'; // Este ficheiro deverá conter as informações de conexão à base de dados

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
            echo "Experiência criada com sucesso.";
        } else {
            echo "Erro ao criar a experiência: " . $stmt->error;
        }
        $stmt->close();
    } else {
        echo "Erro ao preparar a consulta: " . $conn->error;
    }
    $conn->close();
}
?>
