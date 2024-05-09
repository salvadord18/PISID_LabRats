<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php"); // Redireciona para a página para iniciar sessão
    exit();
}

// Obtém o ID da experiência a ser visualizada, que pode ser passado como parâmetro na URL
$experienciaId = $_GET['Experiencia_ID'] ?? null;

if (!$experienciaId) {
    // Exibe uma mensagem de erro e volta para a página anterior
    echo "<script>alert('ID da experiência não fornecido.'); window.history.back();</script>";
}

$stmt = $conn->prepare("CALL VisualizarSubstanciasOdoresExperiencia(?)");
$stmt->bind_param("i", $experienciaId);
$stmt->execute();

// Processar os resultados das substâncias
$resultadoSubstancias = $stmt->get_result();
$substancias = $resultadoSubstancias->fetch_all(MYSQLI_ASSOC);

// Avançar para o próximo resultado para capturar odores
$stmt->next_result();
$resultadoOdores = $stmt->get_result();
$odores = $resultadoOdores->fetch_all(MYSQLI_ASSOC);

$stmt->close();
$conn->close();
?>

<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Substâncias e Odores | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_substancias-odores.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Substâncias e Odores</h1>
    </header>
    <div class="line-container">
        <div class="substancia-section-container">
            <p class="substancia-section-title">Substâncias:</p>
            <?php foreach ($substancias as $substancia): ?>
            <div class="substancia-container">
                <span class="substancia-nome"><?php echo htmlspecialchars($substancia['NomeSubstancia']); ?></span>
                <button class="eliminar-substancia-btn" onclick="location.href='elminar-substancia.php';"></button>
            </div>
            <div class="rats-number-section-container">
                <p class="rats-number-section-title">N.º de ratos:</p>
                <div class="rats-number-container">
                    <span class="rats-number"><?php echo htmlspecialchars($substancia['NumeroRatos']); ?></span>
                </div>
            </div>
            <?php endforeach; ?>
        </div>
        <div class="odor-section-container">
            <p class="odor-section-title">Odores:</p>
            <?php foreach ($odores as $odor): ?>
            <div class="odor-container">
                <span class="odor-nome"><?php echo htmlspecialchars($odor['NomeOdor']); ?></span>
                <button class="eliminar-odor-btn" onclick="location.href='eliminar-odor.php';"></button>
            </div>
            <div class="room-section-container">
                <p class="room-section-title">N.º da sala:</p>
                <div class="room-container">
                    <span class="room-number"><?php echo htmlspecialchars($odor['Sala_ID']); ?></span>
                </div>
            </div>
            <?php endforeach; ?>
        </div>
    </div>
    <div class="line-container addition">
        <div class="substancia-section-container addition select-container">
            <select class="substancia-container">
                <option value="">Selecionar Substância</option>
                <option value="Acido Cloridrico">Ácido Clorídrico</option>
                <option value="Oxigenio">Oxigénio</option>
            </select>
            <input type="number" class="rats-number-container select-container">
            <button class="add-button" onclick="location.href='adicionar-substancia.php';"></button>
        </div>
        <div class="odor-section-container addition select-container add-odor-button-container">
            <select class="odor-container">
                <option value="">Selecionar Odor</option>
                <option value="Salmao">Salmão</option>
                <option value="Banana">Banana</option>
            </select>
            <input type="number" class="room-container select-container">
            <button class="add-button" onclick="location.href='adicionar-odor.php';"></button>
        </div>
    </div>
    <div class="form-actions">
        <button type="submit" class="submit-btn">GUARDAR</button>
    </div>
    <div class="button-container">
        <button type="button" onclick="window.history.back();" class="action-btn back-btn" aria-label="Voltar"></button>
    </div>
    </main>
</body>

</html>