<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alertas | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_alertas.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Alertas</h1>
    </header>
    <div class="alertas-container" id="alertasContainer">
        <!-- Alertas serão inseridos aqui via AJAX -->
        <div id="loadingMessage" class="empty-message">A carregar alertas...</div>
    </div>
    <button type="button" onclick="location.href='/labrats/app/inicio.php';" class="action-btn back-btn" aria-label="Voltar"></button>
    <script>
        function fetchAlerts() {
            $.ajax({
                url: '/labrats/app/get-alertas.php',
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
                                '<button class="visualizar-experiencia-btn" onclick="location.href=\'/labrats/app/visualizar-experiencia2.php?Experiencia_ID=' + alerta.Experiencia_Experiencia_ID + '\';"></button>' +
                                '<p class="experiencia">' + alerta.Experiencia_Experiencia_ID + '</p>' +
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

        // Chame imediatamente para carregar ao abrir a página
        fetchAlerts();
        setInterval(fetchAlerts, 5000); // Atualiza os alertas a cada 5 segundosF
    </script>
</body>

</html>