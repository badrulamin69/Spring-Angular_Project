
const fs = require('fs');
const path = 'src/styles.scss';
let content = fs.readFileSync(path, 'utf8');

const newVars = \
:root {
  --bg-primary: #eef2f6; /* Very soft grayish-blue for main background */
  --bg-secondary: #ffffff; /* White for cards */
  --bg-sidebar: #ffffff; /* White sidebar */
  --bg-tertiary: #f8fafc;
  --bg-hover: rgba(0, 0, 0, 0.04);
  --bg-hover-light: rgba(0, 0, 0, 0.02);
  --bg-hover-strong: rgba(0, 0, 0, 0.08);
  --border-color: rgba(0, 0, 0, 0.06);
  --text-primary: #1e293b;
  --text-secondary: #475569;
  --text-muted: #64748b;
  --topbar-bg: rgba(255, 255, 255, 0.95);
  --brand-color: #10b981; /* Emerald/Green premium color */
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02);
}

body.dark-theme {
  --bg-primary: #090d16;
  --bg-secondary: #0f172a;
  --bg-sidebar: #0f172a;
  --bg-tertiary: #1e293b;
  --bg-hover: rgba(255, 255, 255, 0.05);
  --bg-hover-light: rgba(255, 255, 255, 0.04);
  --bg-hover-strong: rgba(255, 255, 255, 0.1);
  --border-color: rgba(255, 255, 255, 0.08);
  --text-primary: #f1f5f9;
  --text-secondary: #cbd5e1;
  --text-muted: #94a3b8;
  --topbar-bg: rgba(15, 23, 42, 0.92);
  --brand-color: #c7d2fe;
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.5);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.5), 0 2px 4px -1px rgba(0, 0, 0, 0.3);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.5), 0 4px 6px -2px rgba(0, 0, 0, 0.3);
}
\;

// Replace the existing :root and body.dark-theme blocks
content = content.replace(/:root\s*\{[^}]+\}/, ':root_placeholder');
content = content.replace(/body\.dark-theme\s*\{[^}]+\}/, 'body_dark_placeholder');

content = content.replace(':root_placeholder\\n\\nbody_dark_placeholder', newVars);
content = content.replace(':root_placeholder\\r\\n\\r\\nbody_dark_placeholder', newVars);
content = content.replace(/:root_placeholder[\s\S]*?body_dark_placeholder/, newVars);

fs.writeFileSync(path, content, 'utf8');
console.log('Global SCSS variables refactored!');

