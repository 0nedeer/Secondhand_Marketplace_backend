
$content = Get-Content src/main/resources/application.yml -Raw
$repl = "    url: `${DB_URL:jdbc:mysql://localhost:3306/secondhand_trade_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true}`n    username: `${DB_USERNAME:root}`n    password: `${DB_PASSWORD:114514}`"

$content = $content -replace "(?s)<<<<<<< HEAD.*?=======\r?\n.*?>>>>>>> origin/lw", $repl
Set-Content -Path src/main/resources/application.yml -Value $content -NoNewline

