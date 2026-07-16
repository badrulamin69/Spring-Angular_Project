$controllerDir = "F:\Spring-Angular_Project\Uni_MS\uni_ms\src\main\java\com\badrulamin\University_Management\controller"
$results = @()

Get-ChildItem -Path $controllerDir -Filter "*.java" | ForEach-Object {
    $file = $_.Name
    $content = Get-Content $_.FullName -Raw
    
    # Get class-level @RequestMapping
    $classMapping = ""
    $classMatch = [regex]::Matches($content, '@RequestMapping\s*\(\s*value\s*=\s*"([^"]+)"')
    if ($classMatch.Count -eq 0) {
        $classMatch = [regex]::Matches($content, '@RequestMapping\s*\(\s*"([^"]+)"')
    }
    if ($classMatch.Count -gt 0) {
        $classMapping = $classMatch[0].Groups[1].Value
    }
    
    # Match method-level mappings
    $methods = @("RequestMapping", "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping")
    
    foreach ($method in $methods) {
        # Pattern 1: @XxxMapping("path")
        $pattern = "@$method\s*\(\s*`"([^`"]+)`""
        $matches = [regex]::Matches($content, $pattern)
        
        # Pattern 2: @XxxMapping(value = "path")
        $pattern2 = "@$method\s*\(\s*value\s*=\s*`"([^`"]+)`""
        $matches2 = [regex]::Matches($content, $pattern2)
        
        $allMatches = @()
        $allMatches += $matches
        $allMatches += $matches2
        
        foreach ($m in $allMatches) {
            $httpMethod = switch ($method) {
                "RequestMapping" { "ALL" }
                "GetMapping" { "GET" }
                "PostMapping" { "POST" }
                "PutMapping" { "PUT" }
                "DeleteMapping" { "DELETE" }
                "PatchMapping" { "PATCH" }
            }
            
            $path = $m.Groups[1].Value
            
            if ($classMapping -and -not $path.StartsWith("/")) {
                $path = "$classMapping/$path"
            }
            elseif (-not $classMapping -and -not $path.StartsWith("/")) {
                $path = "/$path"
            }
            
            $results += "$file|$httpMethod|$path"
        }
    }
}

$results | Sort-Object | ForEach-Object { Write-Output $_ }
