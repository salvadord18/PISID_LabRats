<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php"); // Redireciona para a página para iniciar sessão
    exit();
}

$experienciaId = $_GET['Experiencia_ID'] ?? null;

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
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var formModified = false;
            var form = document.querySelector('.create-experience-form');

            // Detecta mudanças no formulário
            form.addEventListener('input', function() {
                formModified = true;
            });

            function handleBeforeUnload(e) {
                if (formModified) {
                    var confirmationMessage = 'Tem a certeza de que quer sair? As alterações não serão guardadas.';
                    e.returnValue = confirmationMessage; // Para compatibilidade cross-browser
                    return confirmationMessage;
                }
            }

            // Adiciona o evento beforeunload ao iniciar
            window.addEventListener('beforeunload', handleBeforeUnload);

            // Ao submeter o formulário, remove o evento beforeunload
            form.addEventListener('submit', function() {
                window.removeEventListener('beforeunload', handleBeforeUnload);
            });

            // Redireciona para a página a-aguardar.php com confirmação
            function confirmAndRedirect(event, targetUrl) {
                if (formModified) {
                    var confirmation = confirm('Tem a certeza de que quer sair? As alterações não serão guardadas.');
                    if (!confirmation) {
                        event.preventDefault();
                    } else {
                        window.location.href = targetUrl;
                    }
                } else {
                    window.location.href = targetUrl;
                }
            }

            var backButton = document.querySelector('.back-btn');
            backButton.addEventListener('click', function(event) {
                confirmAndRedirect(event, '/labrats/app/a-aguardar.php?Experiencia_ID=<?php echo $experienciaId; ?>');
            });

            var logoLink = document.querySelector('.logo');
            logoLink.addEventListener('click', function(event) {
                confirmAndRedirect(event, '/labrats/app/a-aguardar2.php?Experiencia_ID=<?php echo $experienciaId; ?>');
            });
        });
    </script>
</head>

<body>
    <header class="header">
        <a class="logo">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Experiencia_<?php echo htmlspecialchars($experienciaId); ?></h1>
    </header>
    <main class="main-content">
        <p style="<?php echo $style; ?>"><?php echo $nomeEstado; ?></p>
        <form action="/labrats/app/guardar-alteracoes-experiencia.php" method="POST" class="create-experience-form">
            <input type="hidden" name="Experiencia_ID" value="<?php echo $experienciaId; ?>">
            <div class="form-field description-field">
                <label for="experience-description">Descrição da experiência:</label>
                <textarea id="experience-description" name="experience_description"><?php echo htmlspecialchars($descricao ?? ''); ?></textarea>
            </div>
            <div class="form-row">
                <div class="form-field">
                    <label for="experience-rats">Número de ratos da experiência:</label>
                    <input type="number" id="experience-rats" name="experience-rats" value="<?php echo htmlspecialchars($numeroRatos ?? ''); ?>" min="1" step="1" required>
                </div>
                <div class="form-field">
                    <label for="experience-rats-limit">Limite de ratos por sala:</label>
                    <input type="number" id="experience-rats-limit" name="experience-rats-limit" value="<?php echo htmlspecialchars($limiteRatosSala ?? ''); ?>" min="1" step="1" required>
                </div>
                <div class="form-field">
                    <label for="experience-seconds">Segundos sem movimento:</label>
                    <input type="number" id="experience-seconds" name="experience-seconds" value="<?php echo htmlspecialchars($segundosSemMovimento ?? ''); ?>" min="0" step="1" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-field">
                    <label for="experience-temperature">Temperatura ideal:</label>
                    <input type="number" id="experience-temperature" name="experience-temperature" value="<?php echo htmlspecialchars($temperaturaIdeal ?? ''); ?>" step="0.1" required>
                </div>
                <div class="form-field">
                    <label for="experience-max-temperature">Variação da temperatura máxima:</label>
                    <input type="number" id="experience-max-temperature" name="experience-max-temperature" value="<?php echo htmlspecialchars($variacaoTemperaturaMaxima ?? ''); ?>" min="0" step="0.1" required>
                </div>
                <div class="form-field">
                    <label for="experience-outliers">Número máximo de outliers:</label>
                    <input type="number" id="experience-outliers" name="experience-outliers" value="<?php echo htmlspecialchars($numeroOutliersMaximo ?? ''); ?>" min="0" step="1" max="100" required>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">GUARDAR ALTERAÇÕES</button>
            </div>
        </form>
        </section>
        <button type="button" onclick="location.href='/labrats/app/experiencias.php';" class="back-btn" aria-label="Voltar"></button>
    </main>
</body>

</html>