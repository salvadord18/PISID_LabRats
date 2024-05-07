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

$stmt = $conn->prepare("CALL VisualizarDadosExperiencia(?, @p_Descricao, @p_NumeroRatos, @p_LimiteRatosSala, @p_SegundosSemMovimento, @p_TemperaturaIdeal, @p_VariacaoTemperaturaMaxima, @p_NumeroOutliersMaximo, @p_Nome_Estado)");
$stmt->bind_param("i", $experienciaId);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    $descricao = $row['Descricao'];
    $numeroRatos = $row['NumeroRatos'];
    $limiteRatosSala = $row['LimiteRatosSala'];
    $segundosSemMovimento = $row['SegundosSemMovimento'];
    $temperaturaIdeal = $row['TemperaturaIdeal'];
    $variacaoTemperaturaMaxima = $row['VariacaoTemperaturaMaxima'];
    $numeroOutliersMaximo = $row['NumeroOutliersMaximo'];
    $nomeEstado = $row['Nome_Estado']; // Supondo que o SP já retorna isso
} else {
    echo "<script>alert('Nenhuma experiência encontrada com o ID fornecido.'); window.history.back();</script>";
    exit();
}

$style = '';
switch ($nomeEstado) {
    case 'A aguardar':
        $style = 'color: #737373;';
        break;
    case 'Edicao':
        $style = 'color: #ffde59;';
        break;
    case 'Em processamento':
        $style = 'color: #5271ff;';
        break;
    case 'Em curso':
            $style = 'color: #7ed957;';
            break;
    case 'Terminada':
            $style = 'color: #ff5757;';
            break;
    case 'Cancelada':
        $style = 'color: #ff5757;';
        break;
    case 'Interrompida':
        $style = 'color: #ff5757;';
        break;
    default:
        $style = 'color: #8C52FF;'; // Um estilo padrão para estados não especificados
}

$stmt->close();
$conn->close();
?>


<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Experiencia_<?php echo htmlspecialchars($experienciaId); ?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_criar-experiencia.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>
<body>
    <header class="header">
        <a href="/labrats/app/inicio.html">
        <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
    </a>
        <h1>Experiencia_<?php echo htmlspecialchars($experienciaId); ?></h1>
    </header>
    <main class="main-content">
        <p style="<?php echo $style; ?>"><?php echo $nomeEstado; ?></p>
        <form action="/labrats/app/ativar-edicao.php" method="POST" class="create-experience-form">
            <input type="hidden" name="experienciaId" value="<?php echo $experienciaId; ?>">
            <div class="form-field description-field">
                <label for="experience-description">Descrição da experiência:</label>
                <textarea id="experience-description" name="experience_description" disabled><?php echo htmlspecialchars($descricao ?? ''); ?></textarea>
            </div>
            <div class="form-row">
                <div class="form-field">
                    <label for="experience-rats">Número de ratos da experiência:</label>
                    <input type="number" id="experience-rats" name="experience-rats" value="<?php echo htmlspecialchars($numeroRatos ?? ''); ?>" disabled>
                </div>
                <div class="form-field">
                    <label for="experience-rats-limit">Limite de ratos por sala:</label>
                    <input type="number" id="experience-rats-limit" name="experience-rats-limit" value="<?php echo htmlspecialchars($limiteRatosSala ?? ''); ?>" disabled>
                </div>
                <div class="form-field">
                    <label for="experience-seconds">Segundos sem movimento:</label>
                    <input type="number" id="experience-seconds" name="experience-seconds" value="<?php echo htmlspecialchars($segundosSemMovimento ?? ''); ?>" disabled>
                </div>
            </div>
            <div class="form-row">
                <div class="form-field">
                    <label for="experience-temperature">Temperatura ideal:</label>
                    <input type="number" id="experience-temperature" name="experience-temperature" value="<?php echo htmlspecialchars($temperaturaIdeal ?? ''); ?>" disabled>
                </div>
                <div class="form-field">
                    <label for="experience-max-temperature">Variação da temperatura máxima:</label>
                    <input type="number" id="experience-max-temperature" name="experience-max-temperature" value="<?php echo htmlspecialchars($variacaoTemperaturaMaxima ?? ''); ?>" disabled>
                </div>
                <div class="form-field">
                    <label for="experience-outliers">Número máximo de outliers:</label>
                    <input type="number" id="experience-outliers" name="experience-outliers" value="<?php echo htmlspecialchars($numeroOutliersMaximo ?? ''); ?>" disabled>
                </div>
            </div>
            <?php if ($nomeEstado === 'A aguardar'): ?>
                <div class="form-actions">
                    <button type="submit" name="action" value="editar" class="submit-btn">EDITAR EXPERIÊNCIA</button>
                </div>
            <?php endif; ?>
            <?php if ($nomeEstado === 'Em processamento'): ?>
                <div class="form-actions">
                    <button type="submit" class="finish-btn">CANCELAR EXPERIÊNCIA</button>
                </div>
            <?php endif; ?>
            <?php if ($nomeEstado === 'Em curso'): ?>
                <div class="form-actions">
                    <button type="submit" class="finish-btn">TERMINAR EXPERIÊNCIA</button>
                </div>
            <?php endif; ?>
            <?php if ($nomeEstado === 'Terminada' || 'Cancelada' || 'Interrompida'): ?>
            <?php endif; ?>
            </form>
        </section>
        <button type="button" onclick="window.history.back();" class="back-btn" aria-label="Voltar"></button>
    </main>
</body>
</html>