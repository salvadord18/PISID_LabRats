<?php
session_start();
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
$experienciaId = $_GET['experiencia_id'] ?? null;
$salaId = $_GET['sala_id'] ?? null;

if (!$userId || !$experienciaId || !$salaId) {
    echo json_encode([]);
    exit;
}

$movimentos = [];
if ($stmt = $conn->prepare("CALL GetMedicaoPassagem(?, ?)")) {
    $stmt->bind_param("ii", $experienciaId, $salaId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    while ($linha = $resultado->fetch_assoc()) {
        $movimentos[] = $linha;
    }
    $stmt->close();
}

$conn->close();

echo json_encode($movimentos);
?>