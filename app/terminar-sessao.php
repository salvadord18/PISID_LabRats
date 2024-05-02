<?php
session_start();  // Inicia a sessão

// Encerra a sessão do utilizador
$_SESSION = array();  // Limpa a sessão
if (ini_get("session.use_cookies")) {
    $params = session_get_cookie_params();
    setcookie(session_name(), '', time() - 42000,
        $params["path"], $params["domain"],
        $params["secure"], $params["httponly"]
    );
}
session_destroy();  // Destrói a sessão

header("Location: iniciar-sessao.html");  // Redireciona para a página de início
exit();
?>
