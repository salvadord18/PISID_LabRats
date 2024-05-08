<!DOCTYPE html>
<html lang="pt">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Substâncias e Odores | LabRats</title>
    <link rel="stylesheet" href="/labrats/css/style_substancias-odores.css">
    <link rel="icon" href="/labrats/icons/icon3.png" type="image/x-icon">
</head>

<body>
    <header class="header">
        <a href="/labrats/inicio.php">
            <img src="/labrats/icons/logo2.png" alt="Lab Rats Logo" class="logo">
        </a>
        <h1>Substâncias e Odores</h1>
    </header>
    <div class="substancia-section-container">
        <p class="substancia-section-title">Substâncias:</p>
        <div class="substancia-container">
            <span class="substancia-nome">Ácido clorídrico</span>
            <button class="eliminar-substancia-btn" onclick="location.href='elminar-substancia.php';"></button>
        </div>
    </div>
    <div class="rats-number-section-container">
        <p class="rats-number-section-title">N.º de ratos:</p>
        <div class="rats-number-container">
            <span class="rats-number">4</span>
        </div>
    </div>
    <div class="odor-section-container">
        <p class="odor-section-title">Odores:</p>
        <div class="odor-container">
            <span class="odor-nome">Salmão</span>
            <button class="eliminar-odor-btn" onclick="location.href='eliminar-odor.php';"></button>
        </div>
    </div>
    <div class="room-section-container">
        <p class="room-section-title">N.º da sala:</p>
        <div class="room-container">
            <span class="room-number">9</span>
        </div>
    </div>
    <div class="form-actions">
        <button type="submit" class="submit-btn">GUARDAR</button>
    </div>
    <div class="button-container">
        <button type="button" onclick="window.history.back();" class="action-btn back-btn" aria-label="Voltar"></button>
    </div>
    </main>
</body>

</html>