import express from 'express';
import favicon from 'serve-favicon';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const port = process.env.PORT || 3000;

app.use(favicon(path.join(__dirname, '../public', 'favicon.ico')));

app.use('/assets', express.static(path.join(__dirname, '../dist/assets')));

app.get('/', (_, res) => {
    res.sendFile(path.join(__dirname, '../dist/index.html'));
});

app.listen(port, () => {
    console.log(`Listening on port ${port}`);
});
