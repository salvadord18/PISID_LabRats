<?php
$servername = "database-1.chugk2cki1rp.us-east-1.rds.amazonaws.com";  // Normalmente localhost
$username = "Pisid25";     // Nome de utilizador da base de dados
$password = "Pisid252024.";     // Palavra-passe da base de dados
$database = "Pisid25"; // Nome da base de dados

// Criar ligação
$conn = new mysqli($servername, $username, $password, $database);

// Verificar ligação
if ($conn->connect_error) {
    die("Falha na ligação: " . $conn->connect_error);
}
echo "Ligação bem-sucedida!";
?>