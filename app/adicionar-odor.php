<?php
session_start();
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Obtém os dados do formulário
$experienciaId = $_POST['experiencia_id'] ?? null;
$odorId = $_POST['odor_id'] ?? null;
$salaId = $_POST['sala_id'] ?? null;

echo "Experiência ID: $experienciaId, Odor ID: $odorId, Sala ID: $salaId";

// Verifica se todos os campos estão preenchidos
if (!$experienciaId || !$odorId || !$salaId) {
    //echo "<script>alert('Por favor, preencha todos os campos.'); window.history.back();</script>";
    exit();
}

try {
    // Prepara e executa a chamada ao procedimento armazenado
    $stmt = $conn->prepare("CALL AdicionarOdorExperiencia(?, ?, ?, @NomeDoOdor)");
    $stmt->bind_param("iii", $experienciaId, $odorId, $salaId);
    $stmt->execute();

    // Obtém o resultado do procedimento armazenado
    $result = $conn->query("SELECT @NomeDoOdor AS NomeDoOdor");
    $data = $result->fetch_assoc();
    $nomeOdor = $data['NomeDoOdor'];

    $stmt->close();
    $conn->close();

    // Redireciona de volta para a página anterior com uma mensagem
    echo "<script>alert('Odor $nomeOdor adicionado à experiência com sucesso.'); window.location.href = '/labrats/app/substancias-odores.php?Experiencia_ID=$experienciaId';</script>";
} catch (mysqli_sql_exception $e) {
    // Em caso de erro, emite um alerta JavaScript com a mensagem de erro
    echo "<script>alert('" . $e->getMessage() . "'); window.history.back();</script>";
}
