<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$username = $_SESSION['username'] ?? null; // Obtém o nome de utilizador da sessão
$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$username || !$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Preparar e executar a consulta à base de dados para obter as experiências do utilizador
$experiencias = [];
if ($stmt = $conn->prepare("CALL MostrarTodasExperienciasInvestigador(?, @p_Nome_Estado)")) {
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    while ($linha = $resultado->fetch_assoc()) {
        $experiencias[] = $linha;
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
    <title>Experiências | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_experiencias.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Experiências</h1>
    </header>
    <main class="experiencias-container">
        <?php if (empty($experiencias)) : ?>
            <p>Não tem experiências criadas. Utilize o botão '+' no canto inferior direito para criar uma.</p>
        <?php else : ?>
            <?php foreach ($experiencias as $experiencia) : ?>
                <?php
                // Define o estilo com base no estado da experiência
                switch ($experiencia['Nome_Estado']) {
                    case 'A aguardar':
                        $style = 'color: #737373;';
                        break;
                    case 'Edicao':
                        $style = 'color: #FFCF00;';
                        break;
                    case 'Em processamento':
                        $style = 'color: #5271ff;';
                        break;
                    case 'Em curso':
                        $style = 'color: #31FF4C;';
                        break;
                    case 'Terminada':
                    case 'Cancelada':
                    case 'Interrompida':
                        $style = 'color: #FF3131;';
                        break;
                    default:
                        $style = 'color: #8C52FF;'; // Um estilo padrão para estados não especificados
                }
                ?>
                <div class="experiencia-container">
                    <span class="experiencia-nome">Experiencia_<?php echo htmlspecialchars($experiencia['Experiencia_ID']); ?></span>
                    <span class="experiencia-estado" style="<?php echo $style; ?>"><?php echo htmlspecialchars($experiencia['Nome_Estado']); ?></span>
                    <button class="view-experience-btn" onclick="location.href='visualizar-experiencia.php?Experiencia_ID=<?php echo $experiencia['Experiencia_ID']; ?>';"></button>
                </div>
                <div class="experiencia-buttons">
                    <button class="substancias-odores-btn" onclick="location.href='/labrats/app/substancias-odores.php?Experiencia_ID=<?php echo $experiencia['Experiencia_ID']; ?>';"></button>
                    <button class="labirinto-btn" onclick="location.href='/labrats/app/labirinto.php?Experiencia_ID=<?php echo $experiencia['Experiencia_ID']; ?>';"></button>
                    <button class="alertas-btn" onclick="location.href='/labrats/app/alertas-experiencia.php?Experiencia_ID=<?php echo $experiencia['Experiencia_ID']; ?>';"></button>
                    <button class="analise-btn" onclick="location.href='/labrats/app/analise.php?Experiencia_ID=<?php echo $experiencia['Experiencia_ID']; ?>';"></button>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
        <div class="button-container">
            <?php if (empty($experiencias)) : ?>
                <button type="button" onclick="location.href='/labrats/app/criar-experiencia.html';" class="action-btn-empty create-btn" aria-label="Adicionar experiência"></button>
                <button type="button" onclick="location.href='/labrats/app/inicio.php';" class="action-btn-empty back-btn" aria-label="Voltar"></button>
            <?php else : ?>
                <button type="button" onclick="location.href='/labrats/app/criar-experiencia.html';" class="action-btn create-btn" aria-label="Adicionar experiência"></button>
                <button type="button" onclick="location.href='/labrats/app/inicio.php';" class="action-btn back-btn" aria-label="Voltar"></button>
            <?php endif; ?>
        </div>
    </main>
</body>

</html>