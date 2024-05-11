<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

$experienciaId = $_GET['Experiencia_ID'] ?? null;

if (!$experienciaId) {
    echo "<script>alert('ID da experiência não fornecido.'); window.history.back();</script>";
    exit();
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
?>

<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alertas da Experiencia_<?php echo htmlspecialchars($experienciaId); ?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_alertas.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Alertas da Experiencia_<?php echo htmlspecialchars($experienciaId); ?></h1>
    </header>
    <main class="alertas-container">
        <?php if (empty($alertas)) : ?>
            <p>Não existem alertas desta experiência.</p>
        <?php else : ?>
            <?php foreach ($alertas as $alerta) : ?>
                <div class="alerta <?= htmlspecialchars(strtolower($alerta['TipoAlerta'])) ?>">
                    <p class="alerta-mensagem <?= htmlspecialchars(strtolower($alerta['TipoAlerta'])) ?>"><?php echo htmlspecialchars($alerta['Mensagem']); ?></p>
                    <p class="alerta-hora <?= htmlspecialchars(strtolower($alerta['TipoAlerta'])) ?>">[<?php echo htmlspecialchars($alerta['Hora']); ?>]</p>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
        <button type="button" onclick="location.href='/labrats/app/experiencias.php';" class="back-btn" aria-label="Voltar"></button>
    </main>
</body>

</html>