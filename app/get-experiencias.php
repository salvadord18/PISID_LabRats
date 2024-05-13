<?php
session_start();
header('Content-Type: application/json');
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    echo json_encode(['error' => 'Utilizador não autenticado']);
    exit;
}

$experiencias = [];
if ($stmt = $conn->prepare("CALL MostrarTodasExperienciasInvestigador(?, @p_Nome_Estado)")) {
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    while ($linha = $resultado->fetch_assoc()) {
        $experiencias[] = $linha;
    }
    $stmt->close();
}
$conn->close();
echo json_encode($experiencias);
?>