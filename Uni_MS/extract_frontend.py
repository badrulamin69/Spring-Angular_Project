import os, re, json

service_dir = r"F:\Spring-Angular_Project\Uni_MS\angular-ui\src\app\services"
controller_dir = r"F:\Spring-Angular_Project\Uni_MS\uni_ms\src\main\java\com\badrulamin\University_Management\controller"

# ===== FRONTEND =====
frontend_endpoints = []

for fname in sorted(os.listdir(service_dir)):
    if not fname.endswith('.ts'):
        continue
    fpath = os.path.join(service_dir, fname)
    with open(fpath, 'r', encoding='utf-8-sig') as f:
        content = f.read()
    
    # Extract apiUrl patterns
    api_url_match = re.search(r'private\s+apiUrl\s*=\s*`?\$\{?environment\.apiUrl\}?`?\s*\+\s*[\'"]([^\'"]+)[\'"]', content)
    if not api_url_match:
        api_url_match = re.search(r'private\s+apiUrl\s*=\s*`?\$\{?environment\.apiUrl\}?/?`?\s*\+\s*[\'"]([^\'"]+)[\'"]', content)
    
    # Find the base path from apiUrl
    base_path = ""
    m = re.search(r'private\s+apiUrl\s*=\s*`\$\{environment\.apiUrl\}(/[^`]*)`', content)
    if m:
        base_path = m.group(1)
    else:
        # Try other patterns
        m2 = re.search(r'private\s+apiUrl\s*=\s*environment\.apiUrl\s*\+\s*[\'"]([^\'"]+)[\'"]', content)
        if m2:
            base_path = m2.group(1)
        else:
            m3 = re.search(r'private\s+apiUrl\s*=\s*[\'"]([^\'"]+)[\'"]', content)
            if m3:
                base_path = m3.group(1)
    
    # Extract all http method calls with URLs
    # Pattern: this.http.METHOD(`...`)  or this.http.METHOD('...')  or this.http.METHOD("...")
    # Need to handle: this.apiUrl, `${this.apiUrl}`, template literals with variables
    
    # Get all HTTP calls
    http_calls = re.finditer(
        r'this\.http\.(get|post|put|delete|patch)\s*<[^>]*>\s*\(\s*(.+?)\s*(?:,\s*\{.*?\})?\s*\)',
        content, re.DOTALL
    )
    
    for call in http_calls:
        method = call.group(1).upper()
        url_expr = call.group(2).strip()
        
        # Normalize the URL
        full_url = None
        
        if '`' in url_expr:
            # Template literal
            # Remove backticks
            url_inner = url_expr.strip('`')
            # Replace ${this.apiUrl} with the base_path
            url_inner = url_inner.replace('${this.apiUrl}', '').replace('${this.apiUrl}', '')
            # Remove ${...} variable parts for endpoint path
            url_inner = re.sub(r'\$\{[^}]+\}', '', url_inner)
            # Clean up
            url_inner = url_inner.strip('/')
            if url_inner:
                full_url = f"/api/{base_path.strip('/')}/{url_inner}" if base_path else f"/api/{url_inner}"
            else:
                full_url = f"/api/{base_path.strip('/')}" if base_path else None
        elif url_expr.startswith("'") or url_expr.startswith('"'):
            # String literal
            url_str = url_expr.strip('\'"')
            if 'environment.apiUrl' in url_str:
                full_url = url_str.replace('environment.apiUrl', '/api')
            else:
                full_url = url_str
        elif 'this.apiUrl' in url_expr:
            # this.apiUrl + '/something'
            m_add = re.search(r"this\.apiUrl\s*\+\s*['\"]([^'\"]+)['\"]", url_expr)
            if m_add:
                path = m_add.group(1)
                path = re.sub(r'\$\{[^}]+\}', '', path).strip('/')
                full_url = f"/api/{base_path.strip('/')}/{path}" if base_path else f"/api/{path}"
            else:
                full_url = f"/api/{base_path.strip('/')}" if base_path else None
        
        if full_url:
            # Normalize: ensure starts with /
            if not full_url.startswith('/'):
                full_url = '/' + full_url
            frontend_endpoints.append((fname, method, full_url))

print("=== FRONTEND ENDPOINTS ===")
for f, m, u in sorted(frontend_endpoints):
    print(f"{f}|{m}|{u}")

print(f"\nTotal frontend endpoints: {len(frontend_endpoints)}")
