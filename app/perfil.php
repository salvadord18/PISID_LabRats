<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$username = $_SESSION['username'] ?? null; // Obtém o nome de utilizador da sessão

if ($username) {
    //$query = "SELECT Utilizador_ID, NomeUtilizador, EmailUtilizador, TelefoneUtilizador FROM utilizador WHERE NomeUtilizador = ?";
    $stmt = $conn->prepare("CALL PerfilUtilizador(?, ?, ?, ?)");
    $stmt->bind_param("s", $username); // 's' indica que o parâmetro é uma string
    $stmt->execute();
    $result = $stmt->get_result();
    if ($user = $result->fetch_assoc()) {
        // Dados recuperados com sucesso
    } else {
        echo "<script>alert('Nenhum utilizador encontrado. " . $stmt->error . "'); window.history.back();</script>";
    }
    $stmt->close();
} else {
    echo "<script>alert('Utilizador não identificado. " . $stmt->error . "'); window.history.back();</script>";
}
$conn->close();
?>

<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Perfil do utilizador | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_perfil.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/app/inicio.html">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Perfil do utilizador</h1>
    </header>
    <main class="profile-container">
        <main class="main-content">
                <div class="form-row">
                    <div class="form-field">
                        <label for="nome-utilizador">Nome de utilizador:</label>
                        <input type="text" id="nome-utilizador" name="nome-utilizador" value="<?php echo htmlspecialchars($user['NomeUtilizador'] ?? ''); ?>" disabled>
                    </div>
                    <div class="form-field">
                            <label for="email">E-mail:</label>
                            <input type="email" id="email" name="email" value="<?php echo htmlspecialchars($user['EmailUtilizador'] ?? ''); ?>" disabled>
                    </div>
                    <div class="form-field">
                            <label for="telefone">Telefone:</label>
                            <input type="integer" id="telefone" name="telefone" value="<?php echo htmlspecialchars($user['TelefoneUtilizador'] ?? ''); ?>" disabled>
                    </div>
                </div>
                <!--<div class="form-row">
                    <div class="form-field">
                        <label for="password">Palavra-passe:</label>
                        <input type="password" id="password" name="password" value="*********" disabled>
                    </div>
                </div>-->
                <div class="profile-actions">
                <form action="/labrats/app/editar-perfil.php" method="post">
                    <button id="edit" class="edit-btn">EDITAR PERFIL</button>
                </form>
                    <form action="/labrats/app/terminar-sessao.php" method="post">
                        <button type="submit" class="logout-btn">TERMINAR SESSÃO</button>
                    </form>
                </div>
                <button type="button" onclick="location.href='/labrats/app/inicio.html';" class="back-btn" aria-label="Voltar"></button>
        </main>
</body>
</html>