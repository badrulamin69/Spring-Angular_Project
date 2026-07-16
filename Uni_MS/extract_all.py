import os, re, glob

service_dir = r"F:\Spring-Angular_Project\Uni_MS\angular-ui\src\app\services"
controller_dir = r"F:\Spring-Angular_Project\Uni_MS\uni_ms\src\main\java\com\badrulamin\University_Management\controller"

# ===== FRONTEND EXTRACTION =====
frontend_endpoints = []

for fname in sorted(os.listdir(service_dir)):
    if not fname.endswith('.ts'):
        continue
    fpath = os.path.join(service_dir, fname)
    with open(fpath, 'r', encoding='utf-8-sig') as f:
        content = f.read()
    
    # Find private apiUrl declaration to get the base path
    base_path = ""
    # Pattern: private apiUrl = `${environment.apiUrl}/students`;
    m = re.search(r'private\s+apiUrl\s*=\s*`\$\{environment\.apiUrl\}(/[^`]*)`', content)
    if m:
        base_path = m.group(1).strip('/')
    else:
        # Pattern: private apiUrl = environment.apiUrl + '/students';
        m2 = re.search(r'private\s+apiUrl\s*=\s*environment\.apiUrl\s*\+\s*[\'"]([^\'"]+)[\'"]', content)
        if m2:
            base_path = m2.group(1).strip('/')
        else:
            # Pattern: private apiUrl = `${environment.apiUrl}`;
            m3 = re.search(r'private\s+apiUrl\s*=\s*`\$\{environment\.apiUrl\}`', content)
            if m3:
                base_path = ""
    
    # Now extract all this.http.METHOD calls
    # We need to handle multiline calls too
    # Strategy: find each this.http. call and extract the URL argument
    
    # Find positions of all http calls
    for m in re.finditer(r'this\.http\.(get|post|put|delete|patch)\s*<', content):
        method = m.group(1).upper()
        start = m.end()
        
        # Find matching parentheses
        depth = 0
        pos = start
        while pos < len(content) and content[pos] != '(':
            pos += 1
        if pos >= len(content):
            continue
        
        # Now find the matching closing paren
        paren_start = pos
        depth = 1
        pos += 1
        while pos < len(content) and depth > 0:
            if content[pos] == '(':
                depth += 1
            elif content[pos] == ')':
                depth -= 1
            pos += 1
        
        args_text = content[paren_start+1:pos-1].strip()
        
        # Extract the first argument (the URL)
        url_arg = None
        
        if args_text.startswith('`'):
            # Template literal - find matching backtick
            end_tick = args_text.find('`', 1)
            if end_tick > 0:
                url_arg = args_text[1:end_tick]
        elif args_text.startswith("'") or args_text.startswith('"'):
            quote = args_text[0]
            end_q = args_text.find(quote, 1)
            if end_q > 0:
                url_arg = args_text[1:end_q]
        
        if url_arg is None:
            continue
            
        # Now resolve the URL
        # Replace ${this.apiUrl} or this.apiUrl references
        url_resolved = url_arg
        
        # Remove ${this.apiUrl} 
        url_resolved = re.sub(r'\$\{this\.apiUrl\}', '', url_resolved)
        
        # Remove ${...} variable interpolations like ${id}, ${studentId} etc
        url_resolved = re.sub(r'\$\{[^}]+\}', '', url_resolved)
        
        # Clean up
        url_resolved = url_resolved.strip('/')
        
        if url_resolved:
            full_url = f"/api/{base_path}/{url_resolved}" if base_path else f"/api/{url_resolved}"
        else:
            full_url = f"/api/{base_path}" if base_path else "/api"
        
        # Normalize slashes
        full_url = re.sub(r'/{2,}', '/', full_url)
        if not full_url.startswith('/'):
            full_url = '/' + full_url
            
        frontend_endpoints.append((fname, method, full_url, url_arg))

# ===== BACKEND EXTRACTION =====
backend_endpoints = []

for fname in sorted(os.listdir(controller_dir)):
    if not fname.endswith('.java'):
        continue
    fpath = os.path.join(controller_dir, fname)
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Get class-level @RequestMapping
    class_mapping = ""
    # Try @RequestMapping(value = "...")
    cm = re.search(r'@RequestMapping\s*\(\s*value\s*=\s*"([^"]+)"', content)
    if not cm:
        cm = re.search(r'@RequestMapping\s*\(\s*"([^"]+)"', content)
    if cm:
        class_mapping = cm.group(1).strip('/')
    
    # Extract method-level mappings
    method_map = {
        'RequestMapping': 'ALL',
        'GetMapping': 'GET', 
        'PostMapping': 'POST',
        'PutMapping': 'PUT',
        'DeleteMapping': 'DELETE',
        'PatchMapping': 'PATCH'
    }
    
    for anno, http_method in method_map.items():
        # Pattern: @XxxMapping("path")
        for m in re.finditer(r'@' + anno + r'\s*\(\s*"([^"]+)"', content):
            path = m.group(1).strip('/')
            if class_mapping:
                full_path = f"/api/{class_mapping}/{path}"
            else:
                full_path = f"/api/{path}"
            full_path = re.sub(r'/{2,}', '/', full_path)
            backend_endpoints.append((fname, http_method, full_path))
        
        # Pattern: @XxxMapping(value = "path")  
        for m in re.finditer(r'@' + anno + r'\s*\(\s*value\s*=\s*"([^"]+)"', content):
            path = m.group(1).strip('/')
            if class_mapping:
                full_path = f"/api/{class_mapping}/{path}"
            else:
                full_path = f"/api/{path}"
            full_path = re.sub(r'/{2,}', '/', full_path)
            backend_endpoints.append((fname, http_method, full_path))
        
        # Pattern: @XxxMapping with no path (just the annotation, defaults to class path)
        if anno != 'RequestMapping':
            # Check if there's a bare @XxxMapping without arguments
            bare_pattern = r'@' + anno + r'\s*\n\s+public'
            if re.search(bare_pattern, content):
                if class_mapping:
                    full_path = f"/api/{class_mapping}"
                    backend_endpoints.append((fname, http_method, full_path))

# Remove duplicates
backend_endpoints = list(set(backend_endpoints))

print("=" * 80)
print("FRONTEND ENDPOINTS")
print("=" * 80)
for f, m, u, orig in sorted(frontend_endpoints):
    print(f"  {f:45s} {m:7s} {u}")

print(f"\nTotal frontend endpoints: {len(frontend_endpoints)}")

print("\n" + "=" * 80)
print("BACKEND ENDPOINTS")  
print("=" * 80)
for f, m, u in sorted(backend_endpoints):
    print(f"  {f:55s} {m:7s} {u}")

print(f"\nTotal backend endpoints: {len(backend_endpoints)}")

# ===== CROSS-REFERENCE =====
print("\n" + "=" * 80)
print("CROSS-REFERENCE ANALYSIS")
print("=" * 80)

# Build lookup tables
# Normalize URLs: remove trailing slashes, consistent format
def normalize(url):
    url = url.rstrip('/')
    url = re.sub(r'/{2,}', '/', url)
    return url

# Frontend: group by (normalized_url, method)
fe_by_url_method = {}
for f, m, u, orig in frontend_endpoints:
    key = (normalize(u), m)
    if key not in fe_by_url_method:
        fe_by_url_method[key] = []
    fe_by_url_method[key].append((f, orig))

# Also store just URL -> methods for frontend
fe_by_url = {}
for f, m, u, orig in frontend_endpoints:
    nu = normalize(u)
    if nu not in fe_by_url:
        fe_by_url[nu] = set()
    fe_by_url[nu].add(m)

# Backend: group by (normalized_url, method)
be_by_url_method = {}
for f, m, u in backend_endpoints:
    key = (normalize(u), m)
    if key not in be_by_url_method:
        be_by_url_method[key] = []
    be_by_url_method[key].append(f)

# Also store just URL -> methods for backend
be_by_url = {}
for f, m, u in backend_endpoints:
    nu = normalize(u)
    if nu not in be_by_url:
        be_by_url[nu] = set()
    be_by_url[nu].add(m)

print("\n--- 1. FRONTEND CALLING NON-EXISTENT BACKEND ENDPOINTS ---")
found_mismatch = False
for (url, method), fe_list in sorted(fe_by_url_method.items()):
    if url in be_by_url:
        if method in be_by_url[url]:
            continue  # Match found
        elif 'ALL' in be_by_url[url]:
            continue  # @RequestMapping matches any method
    # No match
    found_mismatch = True
    for f, orig in fe_list:
        print(f"  MISSING: Frontend {f} calls {method} {url}")
        print(f"           (original URL expression: {orig})")
        # Check if the URL exists with a different method
        if url in be_by_url:
            print(f"           Backend has this URL with methods: {be_by_url[url]}")
        else:
            # Try to find similar URLs
            similar = [u for u in be_by_url.keys() if url.split('/')[-1] in u or u.split('/')[-1] in url.split('/')[-1]]
            if similar:
                print(f"           Possible similar backend URLs: {similar[:5]}")
        print()

if not found_mismatch:
    print("  None found - all frontend endpoints exist in backend!")

print("\n--- 2. BACKEND ENDPOINTS NOT CALLED BY ANY FRONTEND SERVICE ---")
found_orphan = False
for (url, method), be_list in sorted(be_by_url_method.items()):
    if method == 'ALL':
        # @RequestMapping - check if frontend calls this URL with any method
        if url in fe_by_url:
            continue
    else:
        if url in fe_by_url and method in fe_by_url[url]:
            continue
        # Also check if ANY frontend method calls this URL (since ALL covers all)
        if url in fe_by_url:
            continue
    found_orphan = True
    for f in be_list:
        print(f"  ORPHAN: Backend {f} defines {method} {url}")
        if url in fe_by_url:
            print(f"           Frontend calls this URL with methods: {fe_by_url[url]}")
    print()

if not found_orphan:
    print("  None found - all backend endpoints are called by frontend!")

print("\n--- 3. HTTP METHOD MISMATCHES ---")
found_mm = False
for url in set(list(fe_by_url.keys()) + list(be_by_url.keys())):
    fe_methods = fe_by_url.get(url, set())
    be_methods = be_by_url.get(url, set())
    
    if 'ALL' in be_methods:
        continue  # @RequestMapping accepts all methods
    
    for fm in fe_methods:
        if fm not in be_methods and 'ALL' not in be_methods:
            found_mm = True
            print(f"  MISMATCH: {fm} {url}")
            print(f"            Frontend uses: {fm}")
            print(f"            Backend has: {be_methods}")
            # Get the files
            for f, m, u, orig in frontend_endpoints:
                if normalize(u) == url and m == fm:
                    print(f"            Frontend file: {f}")
            for f, m, u in backend_endpoints:
                if normalize(u) == url:
                    print(f"            Backend file: {f}")
            print()

if not found_mm:
    print("  None found!")

print("\n--- 4. URL PATH MISMATCHES (close but not exact) ---")
fe_urls = set(normalize(u) for _, _, u, _ in frontend_endpoints)
be_urls = set(normalize(u) for _, _, u in backend_endpoints)

# Find similar URLs that differ
for fu in sorted(fe_urls):
    fu_parts = fu.rstrip('/').split('/')
    for bu in sorted(be_urls):
        bu_parts = bu.rstrip('/').split('/')
        # Check if they have same number of parts and differ by last part
        if len(fu_parts) == len(bu_parts):
            diffs = sum(1 for a, b in zip(fu_parts, bu_parts) if a != b)
            if diffs == 1 and fu != bu:
                # Check if it's just a plural/singular difference
                fu_last = fu_parts[-1]
                bu_last = bu_parts[-1]
                if (fu_last + 's' == bu_last or fu_last == bu_last + 's' or
                    fu_last.rstrip('s') == bu_last.rstrip('s')):
                    print(f"  POSSIBLE MISMATCH:")
                    print(f"    Frontend: {fu}")
                    print(f"    Backend:  {bu}")
                    # Which files?
                    for f, m, u, orig in frontend_endpoints:
                        if normalize(u) == fu:
                            print(f"    Frontend file: {f} ({m})")
                    for f, m, u in backend_endpoints:
                        if normalize(u) == bu:
                            print(f"    Backend file: {f} ({m})")
                    print()
