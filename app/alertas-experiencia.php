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

?>

<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alertas da Experiencia_<?php echo htmlspecialchars($experienciaId); ?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_alertas.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Alertas da Experiencia_<?php echo htmlspecialchars($experienciaId); ?></h1>
    </header>
    <main class="alertas-experiencia-container" id="alertasContainer">
        <!-- Os alertas serão inseridos aqui via AJAX -->
        <div id="loadingMessage" class="empty-message">A carregar alertas...</div>
    </main>
    <button type="button" onclick="location.href='/labrats/app/experiencias.php';" class="action-btn back-btn" aria-label="Voltar"></button>
    <script>
        function fetchAlerts() {
            $.ajax({
                url: '/labrats/app/get-alertas-experiencia.php?Experiencia_ID=<?php echo $experienciaId; ?>',
                type: 'GET',
                dataType: 'json',
                beforeSend: function() {
                    $('#loadingMessage').show(); // Mostra o carregamento
                },
                success: function(alertas) {
                    var container = $('#alertasContainer');
                    container.empty(); // Limpa tudo para novos dados
                    if (alertas.length > 0) {
                        $.each(alertas, function(index, alerta) {
                            var alertaHtml = '<div class="alerta ' + alerta.TipoAlerta.toLowerCase() + '">' +
                                '<p class="alerta-mensagem ' + alerta.TipoAlerta.toLowerCase() + '">' + alerta.Mensagem + '</p>' +
                                '<p class="alerta-hora ' + alerta.TipoAlerta.toLowerCase() + '">[' + alerta.Hora + ']</p>' +
                                '</div>';
                            container.append(alertaHtml);
                        });
                    } else {
                        container.html('<p class="empty-message">Não existem alertas.</p>'); // Mensagem se não houver alertas
                    }
                },
                complete: function() {
                    $('#loadingMessage').hide(); // Esconde o carregamento
                }
            });
        }

        // Chama a função imediatamente para carregar ao abrir a página
        fetchAlerts();
        setInterval(fetchAlerts, 2000);
    </script>
</body>
</html>