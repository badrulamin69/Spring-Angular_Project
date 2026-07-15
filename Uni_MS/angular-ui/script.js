
const fs = require('fs');
const path = 'src/app/modules/landing/landing.component.ts';
let content = fs.readFileSync(path, 'utf8');

const match = content.match(/styles:\s*\[\([\s\S]*?)\\]/);
if (!match) process.exit(1);

let styles = match[1];

// Base replacements
const colorMap = {
  '#030014': 'var(--bg-base)',
  'rgba(3, 0, 20, 0.85)': 'var(--bg-topbar)',
  'rgba(15, 10, 40, 0.6)': 'var(--bg-dash)',
  'rgba(15, 10, 40, 0.8)': 'var(--bg-dash-solid)',
  '#f8fafc': 'var(--text-primary)',
  '#fff': 'var(--text-primary)',
  '#e2e8f0': 'var(--text-secondary)',
  '#94a3b8': 'var(--text-muted)',
  '#64748b': 'var(--text-muted-dark)',
  'rgba(255, 255, 255, 0.02)': 'var(--border-ultralight)',
  'rgba(255, 255, 255, 0.03)': 'var(--border-verylight)',
  'rgba(255, 255, 255, 0.04)': 'var(--border-light)',
  'rgba(255, 255, 255, 0.05)': 'var(--border-medium)',
  'rgba(255, 255, 255, 0.06)': 'var(--border-heavy)',
  'rgba(255, 255, 255, 0.08)': 'var(--border-strong)',
  'rgba(255, 255, 255, 0.1)': 'var(--border-stronger)',
  'rgba(255, 255, 255, 0.15)': 'var(--border-visible)'
};

for (const [hex, varName] of Object.entries(colorMap)) {
  styles = styles.split(hex).join(varName);
}

const cssVars = \
    :host {
      --bg-base: #f8fafc;
      --bg-topbar: rgba(255, 255, 255, 0.85);
      --bg-dash: rgba(255, 255, 255, 0.9);
      --bg-dash-solid: rgba(255, 255, 255, 0.95);
      --text-primary: #0f172a;
      --text-secondary: #334155;
      --text-muted: #64748b;
      --text-muted-dark: #475569;
      --border-ultralight: rgba(0, 0, 0, 0.02);
      --border-verylight: rgba(0, 0, 0, 0.03);
      --border-light: rgba(0, 0, 0, 0.04);
      --border-medium: rgba(0, 0, 0, 0.05);
      --border-heavy: rgba(0, 0, 0, 0.06);
      --border-strong: rgba(0, 0, 0, 0.08);
      --border-stronger: rgba(0, 0, 0, 0.1);
      --border-visible: rgba(0, 0, 0, 0.15);
    }

    :host-context(.dark-theme) {
      --bg-base: #030014;
      --bg-topbar: rgba(3, 0, 20, 0.85);
      --bg-dash: rgba(15, 10, 40, 0.6);
      --bg-dash-solid: rgba(15, 10, 40, 0.8);
      --text-primary: #f8fafc;
      --text-secondary: #e2e8f0;
      --text-muted: #94a3b8;
      --text-muted-dark: #64748b;
      --border-ultralight: rgba(255, 255, 255, 0.02);
      --border-verylight: rgba(255, 255, 255, 0.03);
      --border-light: rgba(255, 255, 255, 0.04);
      --border-medium: rgba(255, 255, 255, 0.05);
      --border-heavy: rgba(255, 255, 255, 0.06);
      --border-strong: rgba(255, 255, 255, 0.08);
      --border-stronger: rgba(255, 255, 255, 0.1);
      --border-visible: rgba(255, 255, 255, 0.15);
    }
\;

styles = cssVars + '\n' + styles;
content = content.replace(match[1], styles);

fs.writeFileSync(path, content, 'utf8');
console.log('Styles refactored successfully!');

