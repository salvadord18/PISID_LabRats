<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$username = $_SESSION['username'] ?? null; // Obtém o nome de utilizador da sessão

if ($username) {
    $stmt = $conn->prepare("CALL PerfilUtilizador(?)");
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
    <title>Editar perfil | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_editar-perfil.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
    <script>
    function confirmExit() {
        // Verifica se algum campo foi alterado
        var nomeUtilizador = document.getElementById('nome-utilizador').value;
        var email = document.getElementById('email').value;
        var telefone = document.getElementById('telefone').value;

        if (nomeUtilizador !== "<?php echo htmlspecialchars($user['NomeUtilizador'] ?? ''); ?>" || 
            email !== "<?php echo htmlspecialchars($user['EmailUtilizador'] ?? ''); ?>" || 
            telefone !== "<?php echo htmlspecialchars($user['TelefoneUtilizador'] ?? ''); ?>") {
            return "Tem alterações não guardadas. Tem a certeza que quer sair?";
        }
    }

    function disableConfirmExit() {
        window.onbeforeunload = null;
    }

    function enableConfirmExit() {
        window.onbeforeunload = confirmExit;
    }
</script>


</head>

<body onbeforeunload="return confirmExit()">
    <header class="header">
        <a href="/labrats/app/inicio.html">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Editar perfil</h1>
    </header>
    <main class="profile-container">
        <main class="main-content">
            <form action="/labrats/app/guardar-alteracoes-perfil.php" method="POST" class="save-changes-profile-form">
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
                <!--<div class="form-row">
                    <div class="form-field">
                        <label for="password">Palavra-passe:</label>
                        <input type="password" id="password" name="password" value="********">
                    </div>
                </div>-->
                <div class="profile-actions">
                    <button type="submit" id="save" class="save-btn" onclick="disableConfirmExit()">GUARDAR ALTERAÇÕES</button>
                </div>
                <button type="button" onclick="window.history.back();" class="back-btn" aria-label="Voltar"></button>
        </main>
</body>

</html>