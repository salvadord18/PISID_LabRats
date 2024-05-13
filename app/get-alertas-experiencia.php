<?php
session_start();
header('Content-Type: application/json');
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    echo json_encode(['error' => 'Utilizador não autenticado']);
    exit;
}

$experienciaId = $_GET['Experiencia_ID'] ?? null;
if (!$experienciaId) {
    echo json_encode(['error' => 'ID da experiência não fornecido']);
    exit;
}

$alertas = [];
if ($stmt = $conn->prepare("CALL MostrarAlertasExperiencia(?)")) {
    $stmt->bind_param("i", $experienciaId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    while ($linha = $resultado->fetch_assoc()) {
        $alertas[] = $linha;
    }
    $stmt->close();
}
$conn->close();

echo json_encode($alertas);
?>