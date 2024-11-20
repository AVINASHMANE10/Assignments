let workers = [];
let logs = document.getElementById("logs");
let output = document.getElementById("output");

// Utility: Clear logs and output
function clearUI() {
  logs.innerHTML = "";
  output.innerHTML = "";
}
//======here we are displaying time and appending it with time taken BY threads =====
function logMessage(message) {
  const logEntry = document.createElement("div");
  logEntry.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
  logs.appendChild(logEntry);
  logs.scrollTop = logs.scrollHeight; // Scroll to bottom
}
//=============================================================================================

//=============Here we are counting prime numbers using single thread =======================
function calculatePrimesSingleThread(max) {
  const primes = [];
  for (let i = 2; i <= max; i++) {
    let isPrime = true;
    for (let j = 2; j <= Math.sqrt(i); j++) {
      if (i % j === 0) {
        isPrime = false;
        break;
      }
    }
    if (isPrime) primes.push(i);
  }
  return primes;
}

//============this is logic that on click is executed for single thread======================
document.getElementById("singleThreadBtn").addEventListener("click", () => {
  clearUI(); // clears UI i.e. logs and outputs
  const max = parseInt(document.getElementById("primeLimit").value); // access the value present in the element
  logMessage("Starting single-threaded calculation...");

  const start = performance.now(); // here this method calculates current time in milliseconds then agin it is calculated after perticular task to measure time taken by that operation

  const primes = calculatePrimesSingleThread(max);
  const end = performance.now();
  const timeTaken = (end - start).toFixed(2); //converts the time to a string with two decimal places
  logMessage(`Single-threaded calculation completed.`);
  output.innerHTML = `
        <h2>Single-Threaded Results</h2>
        <p>Total primes found: ${primes.length}</p>
        <p>Time taken: ${timeTaken}ms</p>
    `;
}); // here we are printing output No. of prime numbers in 0-limit and time taken

//============this is logic that on click is executed for single thread======================
document.getElementById("multiThreadBtn").addEventListener("click", () => {
  clearUI();
  const max = parseInt(document.getElementById("primeLimit").value);
  const numThreads = parseInt(document.getElementById("numThreads").value); // we are taking total threads we want to create
  const workerThreads = numThreads - 1; // one thread is fixed allocated for main thread for avoiding the UI blocking.

  if (workerThreads < 1) {
    logMessage(
      "Please select at least 2 threads for multi-threaded execution."
    );
    return;
  }

  logMessage("Thank you! Main thread remains responsive.");
  const range = Math.ceil(max / workerThreads); // here MAX.ceil is used to round up answer to nearest integer value also we are distributing the load among the worker threads based on thread numbers
  const primes = []; // main array to store prime numbers
  const threadTimes = [];
  let completedWorkers = 0;
  const start = performance.now();

  // =============================creating worker threads=============================
  for (let i = 0; i < workerThreads; i++) {
    const worker = new Worker("worker-thread.js"); // object of Worker and blueprint is in worker-thread.js

    workers.push(worker); // we add new worker in workers[]

    const threadStart = performance.now(); // time while starting thread
    worker.postMessage([i * range + 1, (i + 1) * range]); // divides data into ranges and sends it to worker-thread because worker thread takes starting and ending point for argument
    worker.onmessage = (e) => {
      try {
        if (e.data.error) {
          logMessage(`Worker ${i + 1} encountered an error: ${e.data.error}`);
        } else {
          primes.push(...e.data.primes); //points to the array of prime numbers that a worker thread has calculated and is sending back to the main thread and that array is added in main prime array.
          const threadEnd = performance.now(); // calculates time taken by thread
          threadTimes.push({
            thread: i + 1,
            timeTaken: (threadEnd - threadStart).toFixed(2),
          });
          completedWorkers++;

          if (completedWorkers === workerThreads) {
            const end = performance.now();
            const totalTime = (end - start).toFixed(2);
            const maxThreadTime = Math.max(
              ...threadTimes.map((t) => parseFloat(t.timeTaken))
            ).toFixed(2); // used to find maxtime taken by thread so we iterate through threadTimes where are Times are available

            // Display results
            let results = `
                            <h2>Multi-Threaded Results</h2>
                            <p>Total primes found: ${primes.length}</p>
                            <p>Total time taken: ${totalTime}ms</p>
                            <p>Longest worker thread time: ${maxThreadTime}ms</p>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Thread</th>
                                        <th>Time Taken (ms)</th>
                                    </tr>
                                </thead>
                                <tbody>
                        `;
            threadTimes.forEach((t) => {
              results += `
                                <tr>
                                    <td>Worker ${t.thread}</td>
                                    <td>${t.timeTaken}</td>
                                </tr>
                            `;
            });
            results += `
                                </tbody>
                            </table>
                        `;
            output.innerHTML = results;
            logMessage("Multi-threaded calculation completed.");
            workers.forEach((w) => w.terminate()); // to terminate workers
            workers = [];
          }
        }
      } catch (error) {
        logMessage(
          `Error in main thread while processing worker ${i + 1}: ${
            error.message
          }`
        );
      }
    };

    worker.onerror = (error) => {
      logMessage(`Worker ${i + 1} encountered an error: ${error.message}`);
    }; // to handle error during execution of worker threads
  }
});
