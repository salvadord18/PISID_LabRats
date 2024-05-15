<?php
session_start();
include 'db.php';

// Verifica se o utilizador está com a sessão iniciada
if (!isset($_SESSION['username'])) {
    header("Location: /labrats/app/iniciar-sessao.html"); // Redirecione para a página de início de sessão
    exit();
}

$utilizadorID = $_SESSION['user_id'];

date_default_timezone_set('Europe/Lisbon');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $descricao = $_POST['experience_description'];
    $dataHora = date('Y-m-d H:i:s');
    $numeroRatos = $_POST['experience-rats'];
    $limiteRatosSala = $_POST['experience-rats-limit'];
    $segundosSemMovimento = $_POST['experience-seconds'];
    $temperaturaIdeal = $_POST['experience-temperature'];
    $variacaoTemperaturaMaxima = $_POST['experience-max-temperature'];
    $numeroOutliersMaximo = $_POST['experience-outliers'];
    $prioridade = $_POST['experience-priority'];

    try {
        $stmt = $conn->prepare("CALL CriarExperiencia(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, @new_Experiencia_ID)");
        $stmt->bind_param("issiiiddii", $utilizadorID, $descricao, $dataHora, $numeroRatos, $limiteRatosSala, $segundosSemMovimento, $temperaturaIdeal, $variacaoTemperaturaMaxima, $numeroOutliersMaximo, $prioridade);
        if ($stmt->execute()) {
            $stmt->close();

            // Recupera o ID da nova experiência inserida
            $result = $conn->query("SELECT @new_Experiencia_ID AS new_Experiencia_ID");
            $row = $result->fetch_assoc();
            if ($row) {
                echo "<script>alert('Experiência criada com sucesso. ID: " . $row['new_Experiencia_ID'] . "'); window.location.href='/labrats/app/experiencias.php';</script>";
            } else {
                echo "<script>alert('Erro ao obter o ID da nova experiência.'); window.history.back();</script>";
            }
        } else {
            echo "<script>alert('Erro ao preparar a consulta: " . addslashes($conn->error) . "'); window.history.back();</script>";
        }
        $conn->close();
    } catch (mysqli_sql_exception $e) {
        // Em caso de erro, emite um alerta JavaScript com a mensagem de erro
        echo "<script>alert('" . $e->getMessage() . "'); window.history.back();</script>";
    }
} else {
    echo "<script>alert('Acesso não autorizado'); window.location.href='/labrats/app/iniciar-sessao.php';</script>";
}
