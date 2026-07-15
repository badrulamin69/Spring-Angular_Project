
const fs = require('fs');
const path = 'src/app/layout/layout.component.scss';
let content = fs.readFileSync(path, 'utf8');

// Add shadows
content = content.replace('border-right: 1px solid var(--border-color);', 'border-right: 1px solid var(--border-color);\n  box-shadow: var(--shadow-sm);');
content = content.replace('border-bottom: 1px solid var(--border-color);', 'border-bottom: 1px solid var(--border-color);\n  box-shadow: var(--shadow-sm);');

// The active nav module pill
content = content.replace('.nav-module-header {\\r\\n  display: flex;', '.nav-module-header {\\r\\n  display: flex;\\r\\n  margin: 2px 12px;\\r\\n  border-radius: 12px;');

fs.writeFileSync(path, content, 'utf8');
console.log('Added premium layout adjustments!');

