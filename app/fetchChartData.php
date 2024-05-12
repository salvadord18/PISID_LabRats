<?php
// fetchChartData.php
include 'db.php';

session_start();
$userId = $_SESSION['user_id'] ?? null;
$experienciaId = $_GET['Experiencia_ID'] ?? null;

if (!$userId || !$experienciaId) {
    echo json_encode([]);
    exit;
}

if ($stmt = $conn->prepare("CALL MostrarAnaliseExperiencia(?)")) {
    $stmt->bind_param("i", $experienciaId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    $dados = $resultado->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
}
$conn->close();

echo json_encode($dados);
?>