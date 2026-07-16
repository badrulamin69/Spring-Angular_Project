const fs = require('fs');
const path = require('path');

const serviceDir = path.join('F:', 'Spring-Angular_Project', 'Uni_MS', 'angular-ui', 'src', 'app', 'services');
const controllerDir = path.join('F:', 'Spring-Angular_Project', 'Uni_MS', 'uni_ms', 'src', 'main', 'java', 'com', 'badrulamin', 'University_Management', 'controller');

// ===== FRONTEND EXTRACTION =====
const frontendEndpoints = [];

const serviceFiles = fs.readdirSync(serviceDir).filter(f => f.endsWith('.ts'));
for (const fname of serviceFiles) {
    const content = fs.readFileSync(path.join(serviceDir, fname), 'utf-8').replace(/^\uFEFF/, '');
    
    let basePath = '';
    let m = content.match(/private\s+apiUrl\s*=\s*`\$\{environment\.apiUrl\}\/([^`]*)`/);
    if (m) {
        basePath = m[1].replace(/\/+$/, '');
    } else {
        m = content.match(/private\s+apiUrl\s*=\s*environment\.apiUrl\s*\+\s*['"]([^'"]+)['"]/);
        if (m) basePath = m[1].replace(/\/+$/, '');
    }
    
    const callRegex = /this\.http\.(get|post|put|delete|patch)\s*</g;
    let callMatch;
    while ((callMatch = callRegex.exec(content)) !== null) {
        const method = callMatch[1].toUpperCase();
        let pos = callMatch.index + callMatch[0].length;
        while (pos < content.length && content[pos] !== '(') pos++;
        if (pos >= content.length) continue;
        
        let depth = 1;
        let parenStart = pos;
        pos++;
        while (pos < content.length && depth > 0) {
            if (content[pos] === '(') depth++;
            else if (content[pos] === ')') depth--;
            pos++;
        }
        
        const argsText = content.substring(parenStart + 1, pos - 1).trim();
        
        let urlArg = null;
        if (argsText.startsWith('`')) {
            const endTick = argsText.indexOf('`', 1);
            if (endTick > 0) urlArg = argsText.substring(1, endTick);
        } else if (argsText.startsWith("'") || argsText.startsWith('"')) {
            const q = argsText[0];
            const endQ = argsText.indexOf(q, 1);
            if (endQ > 0) urlArg = argsText.substring(1, endQ);
        }
        
        if (!urlArg) continue;
        
        let resolved = urlArg
            .replace(/\$\{this\.apiUrl\}/g, '')
            .replace(/\$\{[^}]+\}/g, '')
            .replace(/^\/+/, '')
            .replace(/\/+$/, '');
        
        let fullUrl;
        if (resolved) {
            fullUrl = basePath ? `/api/${basePath}/${resolved}` : `/api/${resolved}`;
        } else {
            fullUrl = basePath ? `/api/${basePath}` : '/api';
        }
        
        fullUrl = fullUrl.replace(/\/{2,}/g, '/');
        if (!fullUrl.startsWith('/')) fullUrl = '/' + fullUrl;
        
        frontendEndpoints.push({ file: fname, method, url: fullUrl, original: urlArg });
    }
}

// ===== BACKEND EXTRACTION =====
const backendEndpoints = [];

const controllerFiles = fs.readdirSync(controllerDir).filter(f => f.endsWith('.java'));
for (const fname of controllerFiles) {
    const content = fs.readFileSync(path.join(controllerDir, fname), 'utf-8');
    
    // Get class-level @RequestMapping
    let classMapping = '';
    let cm = content.match(/@RequestMapping\s*\(\s*value\s*=\s*"([^"]+)"/);
    if (!cm) cm = content.match(/@RequestMapping\s*\(\s*"([^"]+)"/);
    if (cm) classMapping = cm[1].replace(/\/+$/, '');
    
    const methodMap = {
        'GetMapping': 'GET',
        'PostMapping': 'POST',
        'PutMapping': 'PUT',
        'DeleteMapping': 'DELETE',
        'PatchMapping': 'PATCH'
    };
    
    // Handle class-level @RequestMapping as ALL
    // Find all method-level @RequestMapping (not class-level)
    // First, find the class declaration line to know where class body starts
    const classDeclMatch = content.match(/public\s+class\s+\w+/);
    const classBodyStart = classDeclMatch ? classDeclMatch.index : 0;
    
    // Find class-level @RequestMapping position
    const classReqMapping = content.match(/@RequestMapping\s*\(\s*(?:value\s*=\s*)?"([^"]+)"\s*\)/);
    const classReqPos = classReqMapping ? classReqMapping.index : -1;
    
    // Find ALL method-level @RequestMapping annotations (those NOT at class level)
    const reqMappingRegex = /@RequestMapping\s*\(\s*(?:value\s*=\s*)?"([^"]+)"\s*\)/g;
    let rm;
    while ((rm = reqMappingRegex.exec(content)) !== null) {
        // Skip if this is the class-level @RequestMapping
        if (rm.index === classReqPos) continue;
        
        const p = rm[1].replace(/\/+$/, '');
        let fullPath;
        if (classMapping) {
            fullPath = p ? `${classMapping}/${p}` : classMapping;
        } else {
            fullPath = `/api/${p}`;
        }
        backendEndpoints.push({ file: fname, method: 'ALL', url: fullPath.replace(/\/{2,}/g, '/') });
    }
    
    // Handle method-level mappings
    for (const [anno, httpMethod] of Object.entries(methodMap)) {
        // @XxxMapping("path")
        const regex1 = new RegExp(`@${anno}\\s*\\(\\s*"([^"]+)"`, 'g');
        let rm;
        while ((rm = regex1.exec(content)) !== null) {
            const p = rm[1].replace(/\/+$/, '');
            let fullPath;
            if (classMapping) {
                fullPath = p ? `${classMapping}/${p}` : classMapping;
            } else {
                fullPath = `/api/${p}`;
            }
            backendEndpoints.push({ file: fname, method: httpMethod, url: fullPath.replace(/\/{2,}/g, '/') });
        }
        
        // @XxxMapping(value = "path")
        const regex2 = new RegExp(`@${anno}\\s*\\(\\s*value\\s*=\\s*"([^"]+)"`, 'g');
        while ((rm = regex2.exec(content)) !== null) {
            const p = rm[1].replace(/\/+$/, '');
            let fullPath;
            if (classMapping) {
                fullPath = p ? `${classMapping}/${p}` : classMapping;
            } else {
                fullPath = `/api/${p}`;
            }
            backendEndpoints.push({ file: fname, method: httpMethod, url: fullPath.replace(/\/{2,}/g, '/') });
        }
    }
}

// Deduplicate backend
const beSeen = new Set();
const uniqueBackend = backendEndpoints.filter(e => {
    const key = `${e.file}|${e.method}|${e.url}`;
    if (beSeen.has(key)) return false;
    beSeen.add(key);
    return true;
});

// ===== CROSS-REFERENCE =====
function normalize(url) {
    return url.replace(/\/+$/, '').replace(/\/{2,}/g, '/');
}

// Build lookup maps
const feByUrlMethod = {};
const feByUrl = {};
for (const e of frontendEndpoints) {
    const nu = normalize(e.url);
    const key = `${nu}|${e.method}`;
    if (!feByUrlMethod[key]) feByUrlMethod[key] = [];
    feByUrlMethod[key].push(e);
    if (!feByUrl[nu]) feByUrl[nu] = new Set();
    feByUrl[nu].add(e.method);
}

const beByUrlMethod = {};
const beByUrl = {};
for (const e of uniqueBackend) {
    const nu = normalize(e.url);
    const key = `${nu}|${e.method}`;
    if (!beByUrlMethod[key]) beByUrlMethod[key] = [];
    beByUrlMethod[key].push(e);
    if (!beByUrl[nu]) beByUrl[nu] = new Set();
    beByUrl[nu].add(e.method);
}

console.log('='.repeat(80));
console.log('FRONTEND ENDPOINTS');
console.log('='.repeat(80));
for (const e of frontendEndpoints.sort((a, b) => a.file.localeCompare(b.file))) {
    console.log(`  ${e.file.padEnd(45)} ${e.method.padEnd(7)} ${e.url}`);
}
console.log(`\nTotal frontend endpoints: ${frontendEndpoints.length}`);

console.log('\n' + '='.repeat(80));
console.log('BACKEND ENDPOINTS');
console.log('='.repeat(80));
for (const e of uniqueBackend.sort((a, b) => a.file.localeCompare(b.file))) {
    console.log(`  ${e.file.padEnd(55)} ${e.method.padEnd(7)} ${e.url}`);
}
console.log(`\nTotal backend endpoints: ${uniqueBackend.length}`);

// 1. Frontend calling non-existent backend endpoints
console.log('\n' + '='.repeat(80));
console.log('1. FRONTEND CALLING NON-EXISTENT BACKEND ENDPOINTS');
console.log('='.repeat(80));
let found1 = false;
for (const key of Object.keys(feByUrlMethod).sort()) {
    const parts = key.split('|');
    const url = parts[0];
    const method = parts[1];
    const feList = feByUrlMethod[key];
    
    if (beByUrl[url]) {
        if (beByUrl[url].has(method) || beByUrl[url].has('ALL')) continue;
    }
    
    found1 = true;
    for (const e of feList) {
        console.log(`  MISSING: Frontend ${e.file} calls ${method} ${url}`);
        console.log(`           (original URL: ${e.original})`);
        if (beByUrl[url]) {
            console.log(`           Backend has this URL with methods: ${[...beByUrl[url]].join(', ')}`);
        } else {
            const urlLastPart = url.split('/').pop();
            const similar = Object.keys(beByUrl).filter(u => {
                const parts2 = u.split('/');
                return parts2.includes(urlLastPart);
            }).slice(0, 5);
            if (similar.length) console.log(`           Possible similar backend URLs: ${similar.join(', ')}`);
        }
        console.log();
    }
}
if (!found1) console.log('  None found!');

// 2. Backend endpoints not called by any frontend service
console.log('\n' + '='.repeat(80));
console.log('2. BACKEND ENDPOINTS NOT CALLED BY ANY FRONTEND SERVICE');
console.log('='.repeat(80));
let found2 = false;
for (const key of Object.keys(beByUrlMethod).sort()) {
    const parts = key.split('|');
    const url = parts[0];
    const method = parts[1];
    const beList = beByUrlMethod[key];
    
    if (feByUrl[url]) continue;
    
    found2 = true;
    for (const e of beList) {
        console.log(`  ORPHAN: Backend ${e.file} defines ${method} ${url}`);
    }
    console.log();
}
if (!found2) console.log('  None found!');

// 3. HTTP method mismatches
console.log('\n' + '='.repeat(80));
console.log('3. HTTP METHOD MISMATCHES');
console.log('='.repeat(80));
let found3 = false;
const allUrls = new Set([...Object.keys(feByUrl), ...Object.keys(beByUrl)]);
for (const url of [...allUrls].sort()) {
    const feMethods = feByUrl[url] || new Set();
    const beMethods = beByUrl[url] || new Set();
    
    if (beMethods.has('ALL')) continue;
    
    for (const fm of feMethods) {
        if (!beMethods.has(fm)) {
            found3 = true;
            console.log(`  MISMATCH: ${fm} ${url}`);
            console.log(`            Frontend uses: ${fm}`);
            console.log(`            Backend has: ${[...beMethods].join(', ')}`);
            for (const e of frontendEndpoints.filter(e => normalize(e.url) === url && e.method === fm)) {
                console.log(`            Frontend file: ${e.file}`);
            }
            for (const e of uniqueBackend.filter(e => normalize(e.url) === url)) {
                console.log(`            Backend file: ${e.file}`);
            }
            console.log();
        }
    }
}
if (!found3) console.log('  None found!');

// 4. URL path mismatches (similar but not identical)
console.log('\n' + '='.repeat(80));
console.log('4. URL PATH MISMATCHES (close but not exact)');
console.log('='.repeat(80));
const feUrlSet = new Set(frontendEndpoints.map(e => normalize(e.url)));
const beUrlSet = new Set(uniqueBackend.map(e => normalize(e.url)));

let found4 = false;
for (const fu of [...feUrlSet].sort()) {
    for (const bu of [...beUrlSet].sort()) {
        if (fu === bu) continue;
        const fuParts = fu.split('/');
        const buParts = bu.split('/');
        if (fuParts.length !== buParts.length) continue;
        
        let diffs = 0;
        for (let i = 0; i < fuParts.length; i++) {
            if (fuParts[i] !== buParts[i]) diffs++;
        }
        
        if (diffs === 1) {
            const fuLast = fuParts[fuParts.length - 1];
            const buLast = buParts[buParts.length - 1];
            const isPlural = (fuLast + 's' === buLast) || (fuLast === buLast + 's') ||
                            (fuLast.replace(/s$/, '') === buLast.replace(/s$/, ''));
            
            if (isPlural) {
                found4 = true;
                console.log(`  POSSIBLE MISMATCH:`);
                console.log(`    Frontend: ${fu}`);
                console.log(`    Backend:  ${bu}`);
                for (const e of frontendEndpoints.filter(e => normalize(e.url) === fu)) {
                    console.log(`    Frontend file: ${e.file} (${e.method})`);
                }
                for (const e of uniqueBackend.filter(e => normalize(e.url) === bu)) {
                    console.log(`    Backend file: ${e.file} (${e.method})`);
                }
                console.log();
            }
        }
    }
}
if (!found4) console.log('  None found!');
