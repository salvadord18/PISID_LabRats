<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php"); // Redireciona para a página para iniciar sessão
    exit();
}

$experienciaId = $_POST['experienciaId'] ?? null;
if (!$experienciaId) {
    echo "<script>alert('ID da experiência não fornecido.'); window.history.back();</script>";
    exit();
}

// Atualizando o estado da experiência para 'Edição'
$novoEstadoId = 2; // ID do estado 'Edição'
$usuarioId = $_SESSION['user_id']; // ID do usuário da sessão

$stmt = $conn->prepare("UPDATE estadoexperiencia SET Estado_Estado_ID = ? WHERE Experiencia_Experiencia_ID = ?");
$stmt->bind_param("ii", $novoEstadoId, $experienciaId);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    // Redirecionar para a página de edição se o estado foi atualizado com sucesso
    header("Location: /labrats/app/editar-experiencia.php?Experiencia_ID=$experienciaId");
    exit();
} else {
    // Se nenhuma linha foi afetada, possivelmente a experiência já está em 'Edição'
    echo "<script>alert('Erro ao mudar o estado ou estado já é 'Edicao'.'); window.history.back();</script>";
}

$stmt->close();
$conn->close();
?>