self.onmessage = (e) => {
  try {
    const [start, end] = e.data;

    if (start >= end || start < 1) {
      throw new Error("Invalid range for prime calculation.");
    }

    const primes = [];
    for (let i = start; i <= end; i++) {
      let isPrime = true;
      for (let j = 2; j <= Math.sqrt(i); j++) {
        if (i % j === 0) {
          isPrime = false;
          break;
        }
      }
      if (isPrime) primes.push(i);
    }

    self.postMessage({ primes }); // to send results back to main thread
  } catch (error) {
    // Handle exceptions and send error details to the main thread
    self.postMessage({
      error: `Worker encountered an error: ${error.message}`,
    });
  }
};
