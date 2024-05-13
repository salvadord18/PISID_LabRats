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
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</head>
<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Análise da Experiencia_<?php echo htmlspecialchars($experienciaId); ?></h1>
    </header>
    <main class="alertas-container">
        <p id="loadingMessage" class="empty-message">A carregar gráfico...</p>
        <div id="chartContainer" style="height: 370px; width: 100%; display: none;"></div>

        <script>
            function fetchData() {
                fetch('fetchChartData.php?Experiencia_ID=<?php echo $experienciaId; ?>')
                    .then(response => response.json())
                    .then(data => {
                        if (data.length > 0) {
                            document.getElementById("chartContainer").style.display = "block";
                            document.getElementById("loadingMessage").style.display = "none";
                            updateChart(data);
                        } else {
                            document.getElementById("loadingMessage").textContent = "Ainda não é possível ver a análise desta experiência.";
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching data:', error);
                        document.getElementById("loadingMessage").textContent = "Erro ao carregar dados.";
                    });
            }

            function updateChart(data) {
                var chart = new CanvasJS.Chart("chartContainer", {
                    theme: "light2",
                    title: {
                        text: "Ratos por Sala",
                        fontColor: "#8C52FF",
                        fontFamily: "Roboto Mono, monospace",
                    },
                    axisY: {
                        title: "Número de Ratos",
                        titleFontColor: "#8C52FF",
                        fontFamily: "Roboto Mono, monospace",
                    },
                    data: [{
                        type: "column",
                        color: "#8C52FF",
                        dataPoints: data.map(item => ({
                            label: 'Sala ' + item.Sala,
                            y: parseInt(item.NumeroRatos)
                        }))
                    }]
                });
                chart.render();
            }

            setInterval(fetchData, 2000);
            window.onload = fetchData; // Carrega inicialmente os dados
        </script>
    </main>
    <div class="button-container">
        <button type="button" onclick="window.history.back();" class="action-btn back-btn"></button>
    </div>
</body>
</html>