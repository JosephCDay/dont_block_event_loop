module.exports = function somethingSlow(req, res, next) {
    let retData = {primes:[]};
    let end = Date.now() + 10000;
    let i = 2;
    while(Date.now() < end) {
        let prime = true;
        for (let j=2; j < i; j++) {
            if (i % j === 0) {
                prime = false;
                break;
            }
            if (Date.now() > end) {
                res.json(retData).end();
                return;
            }
        }
        if (prime) {
            retData.primes.push(i);
        }
        i++
    }
    res.json(retData).end();
}