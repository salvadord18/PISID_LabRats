<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

if ($userId) {
    $query = "SELECT NomeUtilizador, EmailUtilizador, TelefoneUtilizador FROM utilizador WHERE Utilizador_ID = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    $user = $result->fetch_assoc() ?? null;
    if (!$user) {
        //echo "Nenhum utilizador encontrado.";
        exit();
    }
    $stmt->close();
} else {
    //echo "Utilizador não identificado.";
    exit();
}
$conn->close();
?>

<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar perfil | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_editar-perfil.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/inicio.html">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Editar perfil</h1>
    </header>
    <main class="profile-container">
        <main class="main-content">
            <form action="/labrats/guardar-alteracoes-perfil.php" method="POST" class="save-changes-profile-form">
                <div class="form-row">
                    <div class="form-field">
                        <label for="nome-utilizador">Nome de utilizador:</label>
                        <input type="text" id="nome-utilizador" name="nome-utilizador" style="color: #b3b3b3;" value="<?php echo htmlspecialchars($user['NomeUtilizador'] ?? ''); ?>" disabled>
                    </div>
                    <div class="form-field">
                            <label for="email">E-mail:</label>
                            <input type="email" id="email" name="email" value="<?php echo htmlspecialchars($user['EmailUtilizador'] ?? ''); ?>">
                    </div>
                    <div class="form-field">
                            <label for="telefone">Telefone:</label>
                            <input type="integer" id="telefone" name="telefone" value="<?php echo htmlspecialchars($user['TelefoneUtilizador'] ?? ''); ?>">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-field">
                        <label for="password">Palavra-passe:</label>
                        <input type="password" id="password" name="password" value="********">
                    </div>
                </div>
                <div class="profile-actions">
                    <button type="submit" id="save" class="save-btn">GUARDAR ALTERAÇÕES</button>
                </div>
                <button type="button" onclick="window.history.back();" class="back-btn" aria-label="Voltar"></button>
        </main>
</body>

</html>