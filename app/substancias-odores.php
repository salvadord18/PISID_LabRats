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

// Variável para armazenar o estado atual da experiência
$estadoExperiencia = null;

// Chama o procedimento armazenado para obter o estado atual da experiência
$stmtEstadoExperiencia = $conn->prepare("CALL Get_EstadoExperiencia(?, @p_EstadoAtualExperiencia)");
$stmtEstadoExperiencia->bind_param("i", $experienciaId);
$stmtEstadoExperiencia->execute();
$stmtEstadoExperiencia->close();

// Obtém o valor da variável de saída do procedimento armazenado
$result = $conn->query("SELECT @p_EstadoAtualExperiencia AS p_EstadoAtualExperiencia");
$row = $result->fetch_assoc();
$estadoExperiencia = $row['p_EstadoAtualExperiencia'];

// Chama o procedimento armazenado para obter as substâncias e odores selecionados na experiência
$stmt = $conn->prepare("CALL VisualizarSubstanciasOdoresExperiencia(?)");
$stmt->bind_param("i", $experienciaId);
$stmt->execute();

// Processa os resultados das substâncias e odores
$resultadoSubstanciasExperiencia = $stmt->get_result();
$substanciasExperiencia = $resultadoSubstanciasExperiencia->fetch_all(MYSQLI_ASSOC);

// Avança para o próximo resultado para capturar odores
$stmt->next_result();
$resultadoOdoresExperiencia = $stmt->get_result();
$odoresExperiencia = $resultadoOdoresExperiencia->fetch_all(MYSQLI_ASSOC);

$stmt->close();

// Chama o procedimento armazenado para obter as substâncias não selecionadas na experiência
$stmtSubstanciasNaoSelecionadas = $conn->prepare("CALL GetSubstanciasNaoSelecionadasNaExperiencia(?)");
$stmtSubstanciasNaoSelecionadas->bind_param("i", $experienciaId);
$stmtSubstanciasNaoSelecionadas->execute();

// Processa os resultados das substâncias não selecionadas
$resultadoSubstanciasNaoSelecionadas = $stmtSubstanciasNaoSelecionadas->get_result();
$substanciasNaoSelecionadas = $resultadoSubstanciasNaoSelecionadas->fetch_all(MYSQLI_ASSOC);

$stmtSubstanciasNaoSelecionadas->close();

// Chama o procedimento armazenado para obter todos os odores
$stmtOdores = $conn->prepare("CALL GetOdores()");
$stmtOdores->execute();

// Processa os resultados dos odores
$resultadoOdores = $stmtOdores->get_result();
$odores = $resultadoOdores->fetch_all(MYSQLI_ASSOC);

$stmtOdores->close();

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
                <?php foreach ($substanciasExperiencia as $substancia) : ?>
                    <div class="substancia-container">
                        <span class="substancia-nome"><?php echo htmlspecialchars($substancia['NomeSubstancia']); ?></span>
                        <form action="eliminar-substancia.php" method="POST">
                            <input type="hidden" name="experiencia_id" value="<?php echo $experienciaId; ?>">
                            <input type="hidden" name="SubstanciaExperiencia_ID" value="<?php echo htmlspecialchars($substancia['SubstanciaExperiencia_ID']); ?>">
                            <button class="eliminar-substancia-btn"></button>
                        </form>
                    </div>
                    <div class="rats-number-section-container">
                        <div class="rats-number-container">
                            <span class="rats-number"><?php echo htmlspecialchars($substancia['NumeroRatos']); ?></span>
                        </div>
                    </div>
                <?php endforeach; ?>
                <?php if ($estadoExperiencia == 1) : ?>
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
                <?php endif; ?>
            </div>
        </div>
        <div class="column">
            <div class="odor-section-container">
                <div class="titles-odores">
                    <p class="odor-section-title">Odores:</p>
                    <p class="room-section-title">N.º da sala:</p>
                </div>
                <?php foreach ($odoresExperiencia as $odor) : ?>
                    <div class="odor-container">
                        <span class="odor-nome"><?php echo htmlspecialchars($odor['NomeOdor']); ?></span>
                        <form action="eliminar-odor.php" method="POST">
                            <input type="hidden" name="experiencia_id" value="<?php echo $experienciaId; ?>">
                            <input type="hidden" name="OdorExperiencia_ID" value="<?php echo htmlspecialchars($odor['OdorExperiencia_ID']); ?>">
                            <button class="eliminar-odor-btn"></button>
                        </form>
                    </div>
                    <div class="room-section-container">
                        <div class="room-container">
                            <span class="room-number"><?php echo htmlspecialchars($odor['Sala_ID']); ?></span>
                        </div>
                    </div>
                <?php endforeach; ?>
                <?php if ($estadoExperiencia == 1) : ?>
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
                <?php endif; ?>
            </div>
        </div>
    </div>
    <?php if ($estadoExperiencia == 1) : ?>
        <form action="/labrats/app/experiencias.php" class="form-actions" onsubmit="return confirmarGuardar();">
            <button type="submit" class="submit-btn">GUARDAR</button>
        </form>
    <?php endif; ?>
    <script>
        function confirmarGuardar() {
            alert('Substâncias e Odores guardados com sucesso!');
            return true;
        }
    </script>
    <div class="button-container">
        <button type="button" onclick="location.href='/labrats/app/experiencias.php';" class="action-btn back-btn" aria-label="Voltar"></button>
    </div>
    </main>
</body>

</html>