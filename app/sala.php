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
    <title>Sala <?php echo htmlspecialchars($salaId); ?> da Experiencia_<?php echo $experienciaId ?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_sala.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Sala <?php echo htmlspecialchars($salaId); ?> da Experiencia_<?php echo $experienciaId ?></h1>
    </header>
    <main class="alertas-container">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script>
            $(document).ready(function() {
                function fetchMovimentos() {
                    $.ajax({
                        url: '/labrats/app/get-movimentos.php?experiencia_id=<?php echo $experienciaId; ?>&sala_id=<?php echo $salaId; ?>',
                        type: 'GET',
                        dataType: 'json',
                        success: function(movimentos) {
                            var container = $('.alertas-container');
                            container.empty();
                            if (movimentos.length > 0) {
                                movimentos.forEach(function(mov) {
                                    var mensagem;
                                    if (mov.Sala_Origem_ID == <?php echo $salaId; ?>) {
                                        if (mov.NumeroRatosFinal === null || mov.NumeroRatosFinal === 0) {
                                            mensagem = "Saiu um rato para a Sala " + mov.Sala_Destino_ID + ". Não restam ratos nesta sala.";
                                        } else {
                                            mensagem = "Saiu um rato para a Sala " + mov.Sala_Destino_ID + ". " +
                                                (mov.NumeroRatosFinal == 1 ? "Resta " : "Restam ") +
                                                mov.NumeroRatosFinal + " rato" + (mov.NumeroRatosFinal == 1 ? "" : "s") + " nesta sala.";
                                        }
                                    } else {
                                        if (mov.NumeroRatosFinal === null || mov.NumeroRatosFinal === 0) {
                                            mensagem = "Tinha entrado um rato desde a Sala " + mov.Sala_Origem_ID + " mas entretanto já não restam ratos nesta sala.";
                                        } else {
                                            mensagem = "Entrou um rato desde a Sala " + mov.Sala_Origem_ID + ". " +
                                                (mov.NumeroRatosFinal == 1 ? "Há " : "Há ") +
                                                mov.NumeroRatosFinal + " rato" + (mov.NumeroRatosFinal == 1 ? "" : "s") + " nesta sala.";
                                        }
                                    }

                                    var alertaHtml = '<div class="alerta">' +
                                        '<p class="alerta-mensagem">' + mensagem + '</p>' +
                                        '<p class="alerta-hora">[' + mov.Hora + ']</p>' +
                                        '</div>' +
                                        '<div class="button-container">' +
                                        '<button type="button" onclick="window.history.back();" class="action-btn back-btn" aria-label="Voltar"></button>';
                                    container.append(alertaHtml);
                                });
                            } else {
                                container.html('<p class="empty-message">Ainda não houve movimentos nesta sala.</p>' + '<button type="button" onclick="window.history.back();" class="action-btn back-btn-empty" aria-label="Voltar"></button>');
                            }
                        }
                    });
                }

                fetchMovimentos();
                setInterval(fetchMovimentos, 2000); // Atualiza os dados a cada 5 segundos
            });
        </script>
    </main>
</body>

</html>