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

if ($stmt = $conn->prepare("CALL MostrarAnaliseExperiencia(?)")) {
    $stmt->bind_param("i", $experienciaId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    $dados = [];
    while ($linha = $resultado->fetch_assoc()) {
        $dados[] = $linha;
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
    <title>Análise da Experiencia_<?php echo htmlspecialchars($experienciaId); ?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_analise.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Análise da Experiencia_<?php echo htmlspecialchars($experienciaId); ?></h1>
    </header>
    <?php if (empty($dados)) : ?>
        <p class="none-message">Ainda não é possível ver a análise desta experiência.</p>
    <?php else : ?>
        <main class="alertas-container">
            <div id="chartContainer" style="height: 370px; width: 100%;"></div>
            <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
            <script>
                window.onload = function() {
                    var chart = new CanvasJS.Chart("chartContainer", {
                        theme: "light2",
                        title: {
                            text: "Ratos por Sala",
                            titleFontColor: "#8C52FF",
                        },
                        axisY: {
                            title: "Número de Ratos",
                            titleFontColor: "#8C52FF",
                        },
                        data: [{
                            type: "column",
                            color: "#8C52FF",
                            titleFontColor: "#8C52FF",
                            dataPoints: <?php echo json_encode(array_map(function ($item) {
                                            return ['label' => 'Sala ' . $item['Sala'], 'y' => $item['NumeroRatos']];
                                        }, $dados)); ?>
                        }]
                    });
                    chart.render();
                }
            </script>
        <?php endif; ?>
        <div class="button-container">
            <?php if (empty($dados)) : ?>
                <button type="button" onclick="window.history.back();" class="action-btn back-btn-empty" aria-label="Voltar"></button>
            <?php else : ?>
                <button type="button" onclick="window.history.back();" class="action-btn back-btn" aria-label="Voltar"></button>
            <?php endif; ?>
        </div>
        </main>
</body>

</html>