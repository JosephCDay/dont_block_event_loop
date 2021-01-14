var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var logger = require('morgan');
var http = require('http');
var OpenApiValidator = require('express-openapi-validator');
var somethingSlow = require("./somethingSlow.js");
const { Worker } = require('worker_threads'); // npm install worker


var app = express();

app.use(bodyParser.json());
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
const spec = path.join(__dirname, 'api.yaml');
app.use('/api', express.static(spec));

app.use(
    OpenApiValidator.middleware({
        apiSpec: './api.yaml',
        validateRequests: true, // (default)
        validateResponses: true, // false by default
    }),
);

// Event-loop only
app.get('/primes/loop', somethingSlow);

// Multi-threaded
app.get('/primes/threaded', (req, res) => {
    const worker = new Worker(__dirname + '/threadedSlow.js', { workerData: {} });
    worker.on('message', (ret) => {
        res.json(ret).end();
    });
    worker.on('error', (err) => {
        res.status({ status: 500 }).json({ message: err.message });
    });
    worker.on('exit', (code) => {
        if (code !== 0) {
            res.status({ status: 500 }).json({ message: code });
        }
    });
    worker.postMessage('');
});

app.use((err, req, res, next) => {
    res.status(err.status).json({
        errors: err.errors
    });
});

var server = http.createServer(app);
server.listen(8080);
console.log('Listening on 8080');

module.exports = app;