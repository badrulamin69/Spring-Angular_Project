
const fs = require('fs');
const path = 'src/app/layout/layout.component.scss';
let content = fs.readFileSync(path, 'utf8');

const replacements = [
  { search: 'background: #0f172a;', replace: 'background: var(--bg-sidebar);' },
  { search: 'color: #94a3b8;', replace: 'color: var(--text-muted);' },
  { search: 'border-right: 1px solid rgba(255, 255, 255, 0.06);', replace: 'border-right: 1px solid var(--border-color);' },
  { search: 'border-bottom: 1px solid rgba(255, 255, 255, 0.06);', replace: 'border-bottom: 1px solid var(--border-color);' },
  { search: 'color: #fff;', replace: 'color: #ffffff;' }, // preserve white for logo marks if any
  { search: 'color: #f1f5f9;', replace: 'color: var(--text-primary);' },
  { search: 'color: #64748b;', replace: 'color: var(--text-secondary);' },
  { search: 'background: rgba(255, 255, 255, 0.1);', replace: 'background: var(--bg-hover-strong);' },
  { search: 'background: rgba(255, 255, 255, 0.05);', replace: 'background: var(--bg-hover);' },
  { search: 'background: rgba(255, 255, 255, 0.04);', replace: 'background: var(--bg-hover-light);' },
  { search: 'color: #e2e8f0;', replace: 'color: var(--text-primary);' },
  { search: 'background: rgba(99, 102, 241, 0.12);', replace: 'background: rgba(99, 102, 241, 0.12);' },
  { search: 'color: #c7d2fe;', replace: 'color: var(--brand-color);' },
  { search: 'color: #475569;', replace: 'color: var(--text-muted);' },
  { search: 'color: #cbd5e1;', replace: 'color: var(--text-primary);' },
  { search: 'background: rgba(99, 102, 241, 0.1);', replace: 'background: rgba(99, 102, 241, 0.1);' },
  { search: 'border-top: 1px solid rgba(255, 255, 255, 0.06);', replace: 'border-top: 1px solid var(--border-color);' },
  { search: 'background: #ef4444;', replace: 'background: var(--danger-color, #ef4444);' },
  { search: 'border: 2px solid #fff;', replace: 'border: 2px solid var(--bg-topbar);' },
  { search: 'color: #ef4444;', replace: 'color: var(--danger-color, #ef4444);' },
  { search: 'background: #fef2f2;', replace: 'background: var(--danger-bg, #fef2f2);' }
];

replacements.forEach(r => {
  content = content.split(r.search).join(r.replace);
});

fs.writeFileSync(path, content, 'utf8');
console.log('Layout SCSS refactored for theme variables!');

