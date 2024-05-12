<?php
session_start();
include 'db.php'; // Assegura que este ficheiro tem a tua ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

// Se não houver um utilizador com sessão iniciada, redirecionar para a página de login.
if (!$userId) {
    header("Location: /labrats/app/iniciar-sessao.php");
    exit();
}

// Obtém o ID da experiência a ser visualizada, que pode ser passado como parâmetro na URL
$experienciaId = $_GET['Experiencia_ID'] ?? null;

if (!$experienciaId) {
    // Exibe uma mensagem de erro e volta para a página anterior
    echo "<script>alert('ID da experiência não fornecido.'); window.history.back();</script>";
}

$salas = [];
if ($stmt = $conn->prepare("CALL MostrarLabirinto(?)")) {
    $stmt->bind_param("i", $experienciaId);
    $stmt->execute();
    $resultado = $stmt->get_result();
    while ($linha = $resultado->fetch_assoc()) {
        $salas[] = $linha;
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
    <title>Labirinto da Experiencia_<?php echo $experienciaId?> | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_labirinto.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Labirinto da Experiencia_<?php echo $experienciaId?></h1>
    </header>
    <div class="alertas-container">
        <?php if (empty($salas)) : ?>
            <p>O labirinto não está disponível.</p>
        <?php else : ?>
            <div class="labirinto-container">
                <a href="/labrats/app/sala.php?sala_id=1&experiencia_id=<?php echo htmlspecialchars($experienciaId); ?>" class="sala">1</a>
                <?php foreach ($salas as $labirinto) : ?>
                    <a href="/labrats/app/sala.php?sala_id=<?php echo htmlspecialchars($labirinto['Sala_Destino_ID']); ?>&experiencia_id=<?php echo htmlspecialchars($experienciaId); ?>" class="sala"><?php echo htmlspecialchars($labirinto['Sala_Destino_ID']); ?></a>
                <?php endforeach; ?>
            </div>
        <?php endif; ?>
        <?php if (empty($salas)) : ?>
            <button type="button" onclick="location.href='/labrats/app/experiencias.php';" class="action-btn back-btn-empty" aria-label="Voltar"></button>
        <?php else : ?>
            <button type="button" onclick="location.href='/labrats/app/experiencias.php';" class="action-btn back-btn" aria-label="Voltar"></button>
        <?php endif; ?>
    </div>
    </div>
</body>

</html>