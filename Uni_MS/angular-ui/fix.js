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
    
    let tsContent = fs.readFileSync(tsFile, 'utf8');

    if (tsContent.includes('shared/dynamic-form/dynamic-form.component')) {
        let parts = tsFile.split(path.sep);
        let appIndex = parts.indexOf('app');
        let upCount = parts.length - appIndex - 2; 
        let correctPath = '';
        for(let i=0; i<upCount; i++) correctPath += '../';
        correctPath += 'shared/dynamic-form/dynamic-form.component';

        tsContent = tsContent.replace(/import \{ DynamicFormComponent \} from '.*?';/, `import { DynamicFormComponent } from '${correctPath}';`);

        fs.writeFileSync(tsFile, tsContent, 'utf8');
        console.log('Fixed', tsFile);
    }
}

walkDir(modulesDir, processComponent);
