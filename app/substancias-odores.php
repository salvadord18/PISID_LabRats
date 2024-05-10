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

// Chama o procedimento armazenado para obter as substâncias e odores selecionados na experiência
$stmt = $conn->prepare("CALL VisualizarSubstanciasOdoresExperiencia(?)");
$stmt->bind_param("i", $experienciaId);
$stmt->execute();

// Processa os resultados das substâncias e odores
$resultadoSubstancias = $stmt->get_result();
$substancias = $resultadoSubstancias->fetch_all(MYSQLI_ASSOC);

// Avança para o próximo resultado para capturar odores
$stmt->next_result();
$resultadoOdores = $stmt->get_result();
$odores = $resultadoOdores->fetch_all(MYSQLI_ASSOC);

$stmt->close();

// Chama o procedimento armazenado para obter as substâncias não selecionadas na experiência
$stmtSubstanciasNaoSelecionadas = $conn->prepare("CALL GetSubstanciasNaoSelecionadasNaExperiencia(?)");
$stmtSubstanciasNaoSelecionadas->bind_param("i", $experienciaId);
$stmtSubstanciasNaoSelecionadas->execute();

// Processa os resultados das substâncias não selecionadas
$resultadoSubstanciasNaoSelecionadas = $stmtSubstanciasNaoSelecionadas->get_result();
$substanciasNaoSelecionadas = $resultadoSubstanciasNaoSelecionadas->fetch_all(MYSQLI_ASSOC);

$stmtSubstanciasNaoSelecionadas->close();

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
    <div class="container">
        <div class="column">
            <div class="substancia-section-container">
                <div class="titles-substancias">
                    <p class="substancia-section-title">Substâncias:</p>
                    <p class="rats-number-section-title">N.º de ratos:</p>
                </div>
                <?php foreach ($substancias as $substancia) : ?>
                    <div class="substancia-container">
                        <span class="substancia-nome"><?php echo htmlspecialchars($substancia['NomeSubstancia']); ?></span>
                        <button class="eliminar-substancia-btn" onclick="location.href='eliminar-substancia.php?Experiencia_ID=<?php echo $experienciaId; ?>';"></button>
                    </div>
                    <div class="rats-number-section-container">
                        <div class="rats-number-container">
                            <span class="rats-number"><?php echo htmlspecialchars($substancia['NumeroRatos']); ?></span>
                        </div>
                    </div>
                <?php endforeach; ?>
                <form action="adicionar-substancia.php" method="POST">
                    <input type="hidden" name="experiencia_id" value="<?php echo $experienciaId; ?>">
                    <div class="substancia-addition select-container">
                        <select name="substancia_id" class="substancia-container-addition">
                            <option value="">Selecionar Substância</option>
                            <?php
                            foreach ($substanciasNaoSelecionadas as $substancia) {
                                echo '<option value="' . htmlspecialchars($substancia['Substancia_ID']) . '">' . htmlspecialchars($substancia['NomeSubstancia']) . '</option>';
                            }
                            ?>
                        </select>
                        <div class="rats-number-section-container-addition">
                            <input type="number" name="numero_ratos" class="rats-number-container" min="1">
                        </div>
                        <button type="submit" class="add-button"></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="column">
            <div class="odor-section-container">
                <div class="titles-odores">
                    <p class="odor-section-title">Odores:</p>
                    <p class="room-section-title">N.º da sala:</p>
                </div>
                <?php foreach ($odores as $odor) : ?>
                    <div class="odor-container">
                        <span class="odor-nome"><?php echo htmlspecialchars($odor['NomeOdor']); ?></span>
                        <button class="eliminar-odor-btn" onclick="location.href='eliminar-odor.php?Experiencia_ID=<?php echo $experienciaId; ?>';"></button>
                    </div>
                    <div class="room-section-container">
                        <div class="room-container">
                            <span class="room-number"><?php echo htmlspecialchars($odor['Sala_ID']); ?></span>
                        </div>
                    </div>
                <?php endforeach; ?>
                <form action="adicionar-odor.php" method="POST">
                    <input type="hidden" name="experiencia_id" value="<?php echo $experienciaId; ?>">
                    <div class="odor-addition select-container">
                        <select name="odor_id" class="odor-container-addition">
                            <option value="">Selecionar Odor</option>
                            <?php
                            foreach ($odores as $odor) {
                                echo '<option value="' . htmlspecialchars($odor['Odor_ID']) . '">' . htmlspecialchars($odor['NomeOdor']) . '</option>';
                            }
                            ?>
                        </select>
                        <div class="room-section-container">
                            <input type="number" name="sala_id" class="room-container" min="1">
                        </div>
                        <button class="add-button"></button>
                    </div>
                </form>
            </div>
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