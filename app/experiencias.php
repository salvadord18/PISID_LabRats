<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$userId = $_SESSION['user_id'] ?? null;

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Preparar e executar a consulta à base de dados para obter as experiências do utilizador
$experiencias = [];
if ($stmt = $conn->prepare("SELECT * FROM experiencia WHERE Utilizador_Utilizador_ID = ? AND EstadoRegisto = 'A'")) {
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
    <title>Experiências | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_experiencias.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>
<body>
    <header class="header">
        <a href="/labrats/app/inicio.html">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Experiências</h1>
    </header>
    <main class="experiencias-container">
        <?php if (empty($experiencias)): ?>
            <p>Não tem experiências criadas.</p>
        <?php else: ?>
            <?php foreach ($experiencias as $experiencia): ?>
                <div class="experiencias-container">
        <div class="experiencia-container">
            <span class="experiencia-nome">Experiencia_<?php echo htmlspecialchars($experiencia['Experiencia_ID']); ?></span>
            <button class="view-experience-btn" onclick="location.href='visualizar-experiencia.html';"></button>
        </div>
        <div class="experiencia-buttons">
            <button class="substancias-odores-btn" onclick="location.href='/labrats/app/substancias-odores.html';"></button>
            <button class="labirinto-btn" onclick="location.href='/labrats/app/labirinto.html';"></button>
            <button class="alertas-btn" onclick="location.href='/labrats/app/alertas-experiencia.html';"></button>
            <button class="analise-btn" onclick="location.href='/labrats/app/analise.html';"></button>
        </div>
    </div>
<?php endforeach; ?>
        <?php endif; ?>
        <div class="button-container">
            <button type="button" onclick="location.href='/labrats/app/criar-experiencia.html';" class="action-btn create-btn" aria-label="Adicionar experiência"></button>
            <button type="button" onclick="window.history.back();" class="action-btn back-btn" aria-label="Voltar"></button>
        </div>
    </main>
</body>
</html>