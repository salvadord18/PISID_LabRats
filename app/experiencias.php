<?php
session_start();
include 'db.php';

$userId = $_SESSION['user_id'] ?? null;
if (!$userId) {
    echo json_encode([]);
    exit;
}
?>

<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Experiências | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_experiencias.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            setInterval(fetchExperiencias, 2000);
            fetchExperiencias(); // Carrega inicialmente os dados
        });

        function fetchExperiencias() {
            $.ajax({
                url: '/labrats/app/get-experiencias.php', // Endpoint que retorna as experiências em JSON
                type: 'GET',
                dataType: 'json',
                success: function(experiencias) {
                    var container = $('.experiencias-container');
                    container.empty(); // Limpa a lista atual
                    if (experiencias.length > 0) {
                        experiencias.forEach(function(experiencia) {
                            var style = getColorStyle(experiencia.Nome_Estado);
                            var experienciaHtml = '<div class="experiencia-container">' +
                                '<span class="experiencia-nome">Experiencia_' + experiencia.Experiencia_ID + '</span>' +
                                '<span class="experiencia-estado" style="' + style + '">' + experiencia.Nome_Estado + '</span>' +
                                '<button class="view-experience-btn" onclick="location.href=\'visualizar-experiencia.php?Experiencia_ID=' + experiencia.Experiencia_ID + '\';"></button>' +
                                '</div>' +
                                '<div class="experiencia-buttons">' +
                                '<button class="substancias-odores-btn" onclick="location.href=\'/labrats/app/substancias-odores.php?Experiencia_ID=' + experiencia.Experiencia_ID + '\';"></button>' +
                                '<button class="labirinto-btn" onclick="location.href=\'/labrats/app/labirinto.php?Experiencia_ID=' + experiencia.Experiencia_ID + '\';"></button>' +
                                '<button class="alertas-btn" onclick="location.href=\'/labrats/app/alertas-experiencia.php?Experiencia_ID=' + experiencia.Experiencia_ID + '\';"></button>' +
                                '<button class="analise-btn" onclick="location.href=\'/labrats/app/analise.php?Experiencia_ID=' + experiencia.Experiencia_ID + '\';"></button>' +
                                '</div>' + '<div class="button-container"><button type="button" onclick="location.href=\'/labrats/app/criar-experiencia.html\';" class="action-btn create-btn" aria-label="Adicionar experiência"></button>' +
                                '<button type="button" onclick="location.href=\'/labrats/app/inicio.php\';" class="action-btn back-btn" aria-label="Voltar"></button>'
                                '</div>';
                            container.append(experienciaHtml);

                        });
                    } else {
                        container.html('<p class="empty-message">Não tem experiências criadas. Utilize o botão \'+\' no canto inferior direito para criar uma.</p>' +
                        '<div class="button-container"><button type="button" onclick="location.href=\'/labrats/app/criar-experiencia.html\';" class="action-btn-empty create-btn" aria-label="Adicionar experiência"></button>' + 
                        '<button type="button" onclick="location.href=\'/labrats/app/inicio.php\';" class="action-btn-empty back-btn" aria-label="Voltar"></button></div>');
                    }
                },
                error: function() {
                    console.log('Erro ao obter experiências.');
                }
            });
        }

        function getColorStyle(estado) {
            switch (estado) {
                case 'A aguardar':
                    return 'color: #737373;';
                case 'Edicao':
                    return 'color: #FFCF00;';
                case 'Em processamento':
                    return 'color: #5271ff;';
                case 'Em curso':
                    return 'color: #31FF4C;';
                case 'Terminada':
                case 'Cancelada':
                case 'Interrompida':
                    return 'color: #FF3131;';
                default:
                    return 'color: #8C52FF;'; // Um estilo padrão para estados não especificados
            }
        }
    </script>
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Experiências</h1>
    </header>
    <main class="experiencias-container">
        <!-- Conteúdo dinâmico será inserido aqui -->
    </main>
</body>

</html>