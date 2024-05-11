<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

$salaId = $_GET['sala_id'] ?? null;
$experienciaId = $_GET['experiencia_id'] ?? null;

if (!$salaId || !$experienciaId) {
    echo "<script>alert('ID da sala ou da experiência não fornecido.'); window.history.back();</script>";
    exit();
}

// Array para guardar os movimentos dos ratos
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
?>

<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sala <?php echo htmlspecialchars($salaId); ?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_sala.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Sala <?php echo htmlspecialchars($salaId); ?></h1>
    </header>
    <main class="alertas-container">
        <?php if (empty($movimentos)) : ?>
            <p>Ainda não houve movimentos nesta sala.</p>
        <?php else : ?>
            <?php foreach ($movimentos as $mov) : ?>
                <div class="alerta">
                    <p class="alerta-mensagem">
                        <?php
                        if ($mov['Sala_Origem_ID'] == $salaId) {
                            echo "Saiu um rato para a Sala " . htmlspecialchars($mov['Sala_Destino_ID']) . ". Restam " . htmlspecialchars($mov['NumeroRatosFinal']) . " ratos nesta sala.";
                        } else {
                            echo "Entrou um rato desde a Sala " . htmlspecialchars($mov['Sala_Origem_ID']) . ". Há " . htmlspecialchars($mov['NumeroRatosFinal']) . " ratos nesta sala.";
                        }
                        ?>
                    </p>
                    <p class="alerta-hora">[<?php echo htmlspecialchars($mov['Hora']); ?>]</p>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
        <div class="button-container">
            <?php if (empty($movimentos)) : ?>
                <button type="button" onclick="window.history.back();" class="action-btn back-btn-empty" aria-label="Voltar"></button>
            <?php else : ?>
                <button type="button" onclick="window.history.back();" class="action-btn back-btn" aria-label="Voltar"></button>
            <?php endif; ?>
        </div>
    </main>
</body>

</html>