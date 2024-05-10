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
$substanciaId = $_POST['substancia_id'] ?? null;
$numeroRatos = $_POST['numero_ratos'] ?? null;

// Verifica se todos os campos estão preenchidos
if (!$experienciaId || !$substanciaId || !$numeroRatos) {
    echo "<script>alert('Por favor, preencha todos os campos.'); window.history.back();</script>";
    exit();
}

try {
    // Prepara e executa a chamada ao procedimento armazenado
    $stmt = $conn->prepare("CALL AdicionarSubstanciaExperiencia(?, ?, ?, @NomeDaSubstancia)");
    $stmt->bind_param("iii", $experienciaId, $substanciaId, $numeroRatos);
    $stmt->execute();

    // Obtém o resultado do procedimento armazenado
    $result = $conn->query("SELECT @NomeDaSubstancia AS NomeDaSubstancia");
    $data = $result->fetch_assoc();
    $nomeSubstancia = $data['NomeDaSubstancia'];

    $stmt->close();
    $conn->close();

    // Redireciona de volta para a página anterior com uma mensagem
    echo "<script>alert('Substância $nomeSubstancia adicionada à experiência com sucesso.'); window.location.href = '/labrats/app/substancias-odores.php';</script>";
} catch (mysqli_sql_exception $e) {
    // Em caso de erro, emite um alerta JavaScript com a mensagem de erro
    echo "<script>alert('" . $e->getMessage() . "'); window.history.back();</script>";
}
?>