const { parentPort, workerData } = require('worker_threads');

function threadedSlow() {
    let retData = { primes: [] };
    let end = Date.now() + 10000;
    let i = 2;
    while (Date.now() < end) {
        let prime = true;
        for (let j = 2; j < i; j++) {
            if (i % j === 0) {
                prime = false;
                break;
            }
            if (Date.now() > end) {
                return retData;
            }
        }
        if (prime) {
            retData.primes.push(i);
        }
        i++
    }
    return retData;
}

parentPort.on('message', (param) => {
    const retData = threadedSlow();
    parentPort.postMessage(retData);
});