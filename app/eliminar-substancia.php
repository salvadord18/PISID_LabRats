<?php
session_start();
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Obtém o ID da SubstanciaExperiencia a ser eliminada
$experienciaId = $_POST['experiencia_id'] ?? null;
$subExperienciaId = $_POST['SubstanciaExperiencia_ID'] ?? null;

// Verifica se o ID da SubstanciaExperiencia foi fornecido
if (!$subExperienciaId || !$experienciaId) {
    echo "<script>alert('ID não fornecido.'); window.history.back();</script>";
    exit();
}

try {
    // Prepara e executa a chamada ao procedimento armazenado
    $stmt = $conn->prepare("CALL EliminarSubstanciaExperiencia(?)");
    $stmt->bind_param("i", $subExperienciaId);
    $stmt->execute();
    $stmt->close();
    $conn->close();

    // Redireciona de volta para a página anterior com uma mensagem
    echo "<script>alert('Substância eliminada com sucesso.'); window.location.href = '/labrats/app/substancias-odores.php?Experiencia_ID=$experienciaId';</script>";
} catch (mysqli_sql_exception $e) {
    // Em caso de erro, emite um alerta JavaScript com a mensagem de erro
    echo "<script>alert('" . $e->getMessage() . "'); window.history.back();</script>";
}
?>
