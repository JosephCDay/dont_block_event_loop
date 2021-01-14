# Don't Block the Event-Loop!
Herein are two samples demonstrating the pitfall of creating too much work for the event-loop to process per iteration.
This example is presented in both Vert.x and NodeJS.  It hosts endpoints that calculate as many prime values it can in 10 seconds, requiring the full effort of a thread to  do so.

## Setup

### Java + Vert.x
 - Working folder is in vertx_sample
 - A Java JDK is required.  8 or 11 is recommended.
 - Gradle wrapper is included.
 - Run `gradle install` to install all dependencies.
 - Run `gradle run` to run the app.

### NodeJS
 - Working folder is in node_sample
 - Node v13+ is required
 - NPM is required
 - run `npm install express path body-parser morgan http express-openapi-validator` or just `npm install` and use the included lockfile.
 - run `node main.js`

## Endpoints
In both NodeJS and Vert.x applications when launched, endpoints are served at:

[http://localhost:8080/primes/loop](http://localhost:8080/primes/loop) -
This runs within the main event-loop, and heavy work blocks all other work.  This includes any work on the multi-threaded endpoint.

[http://localhost:8080/primes/threaded](http://localhost:8080/primes/threaded) -
This runs in a thread-pool.  A ton of work can be done at once.  It will still be blocked if the main event loop gets plugged-up as no new connections will complete.

A suitable test is to open 3 browser tabs.  One with [loop](http://localhost:8080/primes/loop) and the other two with [threaded](http://localhost:8080/primes/threaded).
Connect the [loop](http://localhost:8080/primes/loop) tab first, followed by [threaded](http://localhost:8080/primes/threaded) in quick succession thereafter.
You should observe that neither [threaded](http://localhost:8080/primes/threaded) connections can start until [loop](http://localhost:8080/primes/loop) completes.
Once it does, the two [threaded](http://localhost:8080/primes/threaded) jobs will complete in tandem.  This is because the event loop is plugged-up and no other events can complete until done.

As a bonus, I implemented both using [OpenAPI](https://github.com/OAI/OpenAPI-Specification).  Other than a very minor change in the response, they both are using the same OpenAPI contract yaml.
I did not implement any kind of MVC patterns, keeping the code otherwise focused on the problem and its solution: Keeping the Event-Loop unblocked.  There's more to expound upon, and may be explored later, such as properly offloading tasks to Promises for event-handled completion of threaded work.