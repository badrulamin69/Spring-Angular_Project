$serviceDir = "F:\Spring-Angular_Project\Uni_MS\angular-ui\src\app\services"
$results = @()

Get-ChildItem -Path $serviceDir -Filter "*.ts" -Recurse | ForEach-Object {
    $file = $_.Name
    $content = Get-Content $_.FullName -Raw
    
    # Match this.http.get/post/put/delete/patch('url') or this.http.get/post/put/delete/patch("url")
    $pattern = 'this\.http\.(get|post|put|delete|patch)\s*\(\s*[''"]([^''"]+)[''"]\s*[,)]'
    $matches = [regex]::Matches($content, $pattern)
    
    foreach($m in $matches) {
        $method = $m.Groups[1].Value.ToUpper()
        $url = $m.Groups[2].Value
        $results += "$file|$method|$url"
    }
}

$results | Sort-Object | ForEach-Object { Write-Output $_ }
