<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$userId = $_SESSION['user_id'] ?? null;

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$userId) {
    header("Location: login.php");
    exit();
}

// Preparar e executar a consulta à base de dados para obter as experiências do utilizador
$experiencias = [];
if ($stmt = $conn->prepare("SELECT * FROM alerta WHERE Utilizador_Utilizador_ID = ? AND EstadoRegisto = 'A'")) {
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    while ($linha = $resultado->fetch_assoc()) {
        $experiencias[] = $linha;
    }
    $stmt->close();
}
$conn->close();
?>
<!DOCTYPE html>
<html lang="pt">
<head>
<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alertas | LabRats</title>
    <link rel="stylesheet" href="style_alertas.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>
<body>
<header class="header">
        <a href="inicio.html">
        <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
    </a>
        <h1>Alertas</h1>
    </header>
    <main class="experiencias-container">
        <?php foreach ($alertas as $alerta): ?>
            <div class="experiencia">
                <h3><?php echo htmlspecialchars($alerta['']); ?></h3>
            </div>
        <?php endforeach; ?>
        <button type="button" onclick="window.history.back();" class="back-btn" aria-label="Voltar"></button>
    </main>
</body>
</html>