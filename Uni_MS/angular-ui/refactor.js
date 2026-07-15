const fs = require('fs');
const path = require('path');

const modulesDir = path.join(__dirname, 'src', 'app', 'modules');

function walkDir(dir, callback) {
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? walkDir(dirPath, callback) : callback(path.join(dir, f));
    });
}

function processComponent(tsFile) {
    if (!tsFile.endsWith('.component.ts')) return;
    
    const htmlFile = tsFile.replace('.component.ts', '.component.html');
    if (!fs.existsSync(htmlFile)) return;

    let tsContent = fs.readFileSync(tsFile, 'utf8');
    let htmlContent = fs.readFileSync(htmlFile, 'utf8');

    // Skip if it doesn't have a data table, it's not a CRUD module
    if (!htmlContent.includes('<app-data-table') || !tsContent.includes('DataTableComponent')) {
        return;
    }

    // Skip if already refactored
    if (tsContent.includes('DynamicFormComponent')) {
        return;
    }

    // 1. Refactor TS File
    console.log(`Refactoring ${tsFile}...`);
    
    // Import DynamicFormComponent
    let depth = tsFile.split(path.sep).length - modulesDir.split(path.sep).length;
    let upDir = '';
    for(let i=0; i<depth; i++) upDir += '../';
    
    tsContent = `import { DynamicFormComponent } from '${upDir}../../shared/dynamic-form/dynamic-form.component';\n` + tsContent;
    
    // Add to imports array
    tsContent = tsContent.replace(/imports:\s*\[(.*?)\]/, (match, p1) => {
        return `imports: [${p1}, DynamicFormComponent]`;
    });

    // Add properties and methods to class
    const classInjection = `
  showForm = false;
  editingItem: any = null;

  openForm(item?: any) {
    this.editingItem = item || null;
    this.showForm = true;
  }

  saveItem(data: any) {
    if (this.editingItem && this.editingItem.id) {
      this.service.update(this.editingItem.id, data).subscribe({
        next: () => {
          this.showForm = false;
          this.loadData();
        }
      });
    } else {
      this.service.save(data).subscribe({
        next: () => {
          this.showForm = false;
          this.loadData();
        }
      });
    }
  }
`;

    if (tsContent.includes('constructor(')) {
        tsContent = tsContent.replace('constructor(', classInjection + '\n  constructor(');
    } else {
        tsContent = tsContent.replace(/export class .*? \{/, (match) => match + '\n' + classInjection);
    }

    // 2. Refactor HTML File
    htmlContent = htmlContent.replace(/<a[^>]*routerLink=[^>]*>\s*\+\s*Add New\s*<\/a>/, '<button class="btn btn-primary" (click)="openForm()">+ Add New</button>');
    
    htmlContent = htmlContent.replace('<app-data-table', '<app-data-table\n  (onEdit)="openForm($event)"');

    const dynamicFormHtml = `\n\n<app-dynamic-form 
  *ngIf="showForm" 
  [columns]="columns" 
  [initialData]="editingItem" 
  [title]="editingItem ? 'Edit Record' : 'Add New Record'"
  (save)="saveItem($event)" 
  (cancel)="showForm = false">
</app-dynamic-form>\n`;
    htmlContent += dynamicFormHtml;

    fs.writeFileSync(tsFile, tsContent, 'utf8');
    fs.writeFileSync(htmlFile, htmlContent, 'utf8');
}

walkDir(modulesDir, function(filePath) {
    processComponent(filePath);
});
console.log('Refactoring complete!');
