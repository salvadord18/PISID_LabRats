<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Página inicial | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>
<body>
    <header class="header">
        <a href="/labrats/app/inicio.php">
        <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
    </a>
    <?php
        session_start();
        $username = $_SESSION['username'] ?? "Utilizador";
        echo "<h1>Bem-vindo, $username!</h1>";
    ?>
    </header>
    <div class="navigation">
        <ul>
            <li>
                <a href="/labrats/app/perfil.php" class="nav-item">
                    <img src="/labrats/icons/perfil.png" alt="" class="icon">
                    <span>Perfil do utilizador</span>
                </a>
            </li>
            <li>
                <a href="/labrats/app/experiencias.php" class="nav-item">
                    <img src="/labrats/icons/experiencias.png" alt="" class="icon">
                    <span>Experiências</span>
                </a>
            </li>
            <li>
                <a href="/labrats/app/alertas.php" class="nav-item">
                    <img src="/labrats/icons/alertas.png" alt="" class="icon">
                    <span>Alertas</span>
                </a>
            </li>
        </ul>
    </div>
    </div>
    <script src="script.js"></script>
</body>
</html>
