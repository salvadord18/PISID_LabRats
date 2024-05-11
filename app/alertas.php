<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

$alertas = [];
if ($stmt = $conn->prepare("CALL MostrarTodosAlertas(?)")) {
    $stmt->bind_param("i", $userId);
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
    <title>Alertas | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_alertas.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Alertas</h1>
    </header>
    <div class="alertas-container">
        <?php if (empty($alertas)) : ?>
            <p>Não existem alertas.</p>
        <?php else : ?>
            <?php foreach ($alertas as $alerta) : ?>
                <div class="alerta <?= htmlspecialchars(strtolower($alerta['TipoAlerta'])) ?>">
                    <p class="alerta-mensagem <?= htmlspecialchars(strtolower($alerta['TipoAlerta'])) ?>"><?php echo htmlspecialchars($alerta['Mensagem']); ?></p>
                    <p class="alerta-hora <?= htmlspecialchars(strtolower($alerta['TipoAlerta'])) ?>">[<?php echo htmlspecialchars($alerta['Hora']); ?>]</p>
                    <button class="visualizar-experiencia-btn" onclick="location.href='/labrats/app/visualizar-experiencia.php?Experiencia_ID=<?php echo $alerta['Experiencia_Experiencia_ID']; ?>';"></button>
                    <p class="experiencia" onclick="location.href='/labrats/app/visualizar-experiencia.php?Experiencia_ID=<?php echo $alerta['Experiencia_Experiencia_ID']; ?>';"><?php echo $alerta['Experiencia_Experiencia_ID']; ?></p>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
        <button type="button" onclick="location.href='/labrats/app/inicio.php';" class="back-btn" aria-label="Voltar"></button>
    </div>
</body>

</html>