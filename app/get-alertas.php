<?php
session_start();
header('Content-Type: application/json');
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    echo json_encode(['error' => 'Utilizador não autenticado']);
    exit;
}

$response = [];

if ($userId) {
    if ($stmt = $conn->prepare("CALL MostrarTodosAlertas(?)")) {
        $stmt->bind_param("i", $userId);
        $stmt->execute();
        $resultado = $stmt->get_result();
        while ($linha = $resultado->fetch_assoc()) {
            $response[] = $linha;
        }
        $stmt->close();
    }
    $conn->close();
}

echo json_encode($response);
?>