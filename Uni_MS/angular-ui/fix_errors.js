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

const oldSaveItem = `  saveItem(data: any) {
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
  }`;

const newSaveItem = `  saveItem(data: any) {
    const handleSuccess = () => {
      this.showForm = false;
      this.loadData();
    };
    const handleError = (err: any) => {
      alert('Error saving record: ' + (err.error?.message || err.message || 'Validation failed'));
    };

    if (this.editingItem && this.editingItem.id) {
      this.service.update(this.editingItem.id, data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    } else {
      this.service.save(data).subscribe({
        next: handleSuccess,
        error: handleError
      });
    }
  }`;

// And the roleService/userService variants
const oldSaveItemRole = oldSaveItem.replace(/this\.service\./g, 'this.roleService.');
const newSaveItemRole = newSaveItem.replace(/this\.service\./g, 'this.roleService.');

const oldSaveItemUser = oldSaveItem.replace(/this\.service\./g, 'this.userService.');
const newSaveItemUser = newSaveItem.replace(/this\.service\./g, 'this.userService.');


function processComponent(tsFile) {
    if (!tsFile.endsWith('.component.ts')) return;
    
    let tsContent = fs.readFileSync(tsFile, 'utf8');
    let changed = false;

    if (tsContent.includes(oldSaveItem)) {
        tsContent = tsContent.replace(oldSaveItem, newSaveItem);
        changed = true;
    } else if (tsContent.includes(oldSaveItemRole)) {
        tsContent = tsContent.replace(oldSaveItemRole, newSaveItemRole);
        changed = true;
    } else if (tsContent.includes(oldSaveItemUser)) {
        tsContent = tsContent.replace(oldSaveItemUser, newSaveItemUser);
        changed = true;
    }

    if (changed) {
        fs.writeFileSync(tsFile, tsContent, 'utf8');
        console.log('Fixed error handling in', tsFile);
    }
}

walkDir(modulesDir, processComponent);

// Also fix students columns
let studentFile = path.join(modulesDir, 'students', 'list', 'list.component.ts');
if (fs.existsSync(studentFile)) {
    let content = fs.readFileSync(studentFile, 'utf8');
    if (!content.includes('studentCode')) {
        content = content.replace("{ key: 'email', label: 'Email' }", "{ key: 'email', label: 'Email' },\n    { key: 'studentCode', label: 'Student Code' }");
        fs.writeFileSync(studentFile, content, 'utf8');
        console.log('Added studentCode to list.component.ts columns');
    }
}
