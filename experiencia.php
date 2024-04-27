<?php
session_start();
include 'db.php'; // Inclui o script de ligação à base de dados

$userId = $_SESSION['user_id'] ?? null; // Obtém o ID do utilizador da sessão

if (!$userId) {
    header("Location: login.php"); // Redireciona para a página de login se não estiver logado
    exit();
}

// Inicialize as variáveis
$experiencia = null;
$mensagemEstado = '';
$classeEstado = '';

// Vamos supor que a experiência tem um campo 'estado' na base de dados que pode ser 'aguardando', 'em_andamento', 'concluida', etc.
$query = "SELECT * FROM experiencia WHERE Utilizador_ID = ? AND Estado != 'concluida'"; // Esta query busca experiências não concluídas do usuário
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $userId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $experiencia = $result->fetch_assoc();

    // Verifique o estado e defina as mensagens e classes CSS correspondentes
    switch ($experiencia['Estado']) {
        case 'aguardando':
            $mensagemEstado = 'A experiência está aguardando para começar.';
            $classeEstado = 'estado-aguardando';
            break;
        case 'em_andamento':
            $mensagemEstado = 'A experiência está atualmente em andamento.';
            $classeEstado = 'estado-em-andamento';
            break;
        // Adicione mais estados conforme necessário
    }
} else {
    $mensagemEstado = 'Nenhuma experiência ativa encontrada.';
    $classeEstado = 'estado-inativo';
}
$stmt->close();
$conn->close();
?>

<!-- A partir daqui começa o HTML, onde pode usar as variáveis PHP acima para mostrar informações condicionais -->
<!DOCTYPE html>
<html lang="pt">
<head>
    <!-- Cabeçalho aqui -->
</head>
<body>
    <header class="header">
        <!-- Cabeçalho aqui -->
    </header>
    <main class="main-content">
        <!-- Utilize as classes de estado para mudar a cor com base no estado -->
        <p class="form-instructions <?php echo $classeEstado; ?>">
            <?php echo $mensagemEstado; ?>
        </p>
        
        <!-- Aqui você pode ter diferentes botões baseados no estado da experiência -->
        <?php if ($experiencia && $experiencia['Estado'] == 'aguardando'): ?>
            <!-- Botões para experiências aguardando -->
        <?php elseif ($experiencia && $experiencia['Estado'] == 'em_andamento'): ?>
            <!-- Botões para experiências em andamento -->
        <?php endif; ?>

        <!-- Resto do conteúdo aqui -->
    </main>
</body>
</html>
