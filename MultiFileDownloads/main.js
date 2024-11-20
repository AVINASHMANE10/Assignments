const downloadList = document.getElementById("downloadList");
const startDownloadBtn = document.getElementById("startDownload");
const fileUrlsInput = document.getElementById("fileUrls");
const useDefaultUrlsCheckbox = document.getElementById("useDefaultUrls");
const threadCountInput = document.getElementById("threadCount");
const logDiv = document.getElementById("log");

const downloads = new Map();
let threadWorkers = [];
let isOnline = navigator.onLine; // to check internet status

const defaultUrls = [
  "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
  "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
  "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
  "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
];

// ==============================To start download ========================
startDownloadBtn.addEventListener("click", async () => {
  const useDefault = useDefaultUrlsCheckbox.checked; // to use default check boxs
  const urls = useDefault ? defaultUrls : processUrls(fileUrlsInput.value);

  if (urls.length === 0) {
    alert("Please provide at least one valid URL or use the default URLs.");
    return;
  }

  const threadCount = Math.min(parseInt(threadCountInput.value, 10) || 1, 8); //here we are validating the thread user input field which is taken from user validation states that if value entered into threadCountInput is invalid then by default 1 is taken else if user enters value bigger than 8 Math.min will select minimum value from arguments which is 8.

  clearLog();
  log(`Starting downloads with ${threadCount} threads...`);

  //=====================downloading using Multiple threads===================
  // Measure multi-threaded execution time
  const multiThreadStartTime = performance.now();
  await distributeDownloads(urls, threadCount); // here downloading task is distribiuted among all threads where we pass all urls  and total workerthreads.
  const multiThreadEndTime = performance.now();

  const multiThreadTotalTime = (
    (multiThreadEndTime - multiThreadStartTime) /
    1000
  ).toFixed(2);
  log(
    `Total download time with ${threadCount} threads: ${multiThreadTotalTime} seconds.`
  ); // in this function we calculate time required to complete downloading using all worker threads then we convert that milliseconds into second by dividing by 1000 then for precise reading result is converted into two decimal places.
  //=================================================================

  //=====================downloading using single thread===================
  log(`Measuring single-threaded execution time for comparison...`);
  const singleThreadTotalTime = await measureSingleThread(urls); // calling measureSingleThread for downloading using single thread
  log(
    `Total download time with a single thread: ${singleThreadTotalTime.toFixed(
      2
    )} seconds.`
  ); // here time taken by single thread is printed in two decimal place format
  log(
    `Multi-threading speedup: ${(
      singleThreadTotalTime / multiThreadTotalTime
    ).toFixed(2)}x faster.`
  ); // here we are comparing both Single/Multithread
});
//==========================================================================

//========== processing and validating URLs ==============================
function processUrls(input) {
  return input
    .split("\n")
    .map((url) => url.trim())
    .filter((url) => validateUrl(url));
} // here we seperate URLs \n splits string into array of sting then whitespaces are removed using .trim() then each url is validated by passing urls into

function validateUrl(url) {
  try {
    new URL(url); //create URL object which gives access to basic URL functionalities also helps to validates URL based n structure.
    return true;
  } catch {
    log(`Invalid URL skipped: ${url}`);
    return false;
  }
}
//=====================================================================

//==================distributing and assigning the Downloads to threads===========
async function distributeDownloads(urls, threadCount) {
  threadWorkers = Array.from({ length: threadCount }, () => ({
    worker: new Worker("worker.js"),
    active: false,
  })); // creates array with length of value assigned to threadCount then add workers object for each index by setting active status false to each object.

  const promises = urls.map((url, i) =>
    assignToThread(url, threadWorkers[i % threadWorkers.length])
  ); // assignes the urls to thread for downloading .

  await Promise.all(promises);
}

function assignToThread(url, thread) {
  return new Promise((resolve, reject) => {
    thread.active = true;

    const id = `download-${Date.now()}-${Math.random().toString(36).slice(2)}`;
    const downloadItem = createDownloadItem(id, url);

    thread.worker.onmessage = (event) => {
      const { progress, status, error, file, fileName } = event.data;

      if (status === "progress") {
        updateProgress(id, progress);
      } else if (status === "complete" && file) {
        const link = document.createElement("a");
        link.href = URL.createObjectURL(file);
        link.download = fileName;
        link.click();
        completeDownload(id, thread);
        resolve();
      } else if (status === "error") {
        handleError(id, error);
        reject(error);
      }
    };

    thread.worker.onerror = (e) => {
      handleError(id, e.message);
      reject(e.message);
    };

    downloads.set(id, { thread, url, paused: false });
    thread.worker.postMessage({ action: "start", fileUrl: url });
  });
}

function createDownloadItem(id, url) {
  const div = document.createElement("div");
  div.className = "download-item";
  div.id = id;
  div.innerHTML = `
    <div class="progress-bar"><span style="width: 0%;"></span></div>
    <div class="download-actions">
      <button onclick="pauseDownload('${id}')">Pause</button>
      <button onclick="resumeDownload('${id}')" disabled>Resume</button>
      <button onclick="cancelDownload('${id}')">Cancel</button>
    </div>
  `;
  downloadList.appendChild(div);
}

function updateProgress(id, progress) {
  const progressBar = document.querySelector(
    `#${CSS.escape(id)} .progress-bar span`
  );
  if (progressBar) {
    progressBar.style.width = `${progress}%`;
  }
}

function completeDownload(id, thread) {
  downloads.delete(id);
  thread.active = false;
  document.getElementById(id)?.remove();
}

function handleError(id, error) {
  downloads.delete(id);
  document.getElementById(id)?.remove();
  alert(`Download failed: ${error}`);
}

function pauseDownload(id) {
  const download = downloads.get(id);
  if (download && !download.paused) {
    download.thread.worker.postMessage({ action: "pause" });
    download.paused = true;

    const resumeBtn = document.querySelector(
      `#${CSS.escape(id)} .download-actions button:nth-child(2)`
    );
    resumeBtn.disabled = false;
  }
}

function resumeDownload(id) {
  const download = downloads.get(id);
  if (download && download.paused) {
    download.thread.worker.postMessage({ action: "resume" });
    download.paused = false;

    const resumeBtn = document.querySelector(
      `#${CSS.escape(id)} .download-actions button:nth-child(2)`
    );
    resumeBtn.disabled = true;
  }
}

function cancelDownload(id) {
  const download = downloads.get(id);
  if (download) {
    download.thread.worker.terminate();
    downloads.delete(id);
    document.getElementById(id)?.remove();
  }
}

// Measure single-threaded download time
async function measureSingleThread(urls) {
  log(`Starting single-threaded download for ${urls.length} files...`);

  const singleWorker = new Worker("worker.js");
  let completedDownloads = 0;
  const startTime = performance.now();

  for (const url of urls) {
    try {
      await new Promise((resolve, reject) => {
        singleWorker.onmessage = (event) => {
          const { status, error, file } = event.data;
          if (status === "complete" && file) {
            completedDownloads++;
            log(`Single-thread: Download complete for ${url}`);
            resolve();
          } else if (status === "error") {
            log(`Single-thread: Error encountered for ${url}: ${error}`);
            reject(error);
          }
        };

        singleWorker.onerror = (e) => {
          log(`Single-thread: Worker error: ${e.message}`);
          reject(e.message);
        };

        singleWorker.postMessage({ action: "start", fileUrl: url });
      });
    } catch (error) {
      log(`Single-thread: Error while downloading ${url}: ${error}`);
    }
  }

  const endTime = performance.now();
  singleWorker.terminate();
  return (endTime - startTime) / 1000;
}

// Monitor Network Status
window.addEventListener("offline", () => {
  log("Internet connection lost. Pausing all downloads...");
  isOnline = false;
  downloads.forEach((download, id) => {
    if (!download.paused) {
      pauseDownload(id);
    }
  });
});

window.addEventListener("online", () => {
  log("Internet connection reestablished. Resuming downloads...");
  isOnline = true;
  downloads.forEach((download, id) => {
    if (download.paused) {
      resumeDownload(id);
    }
  });
});

function clearLog() {
  logDiv.innerHTML = "";
}

function log(message) {
  const logEntry = document.createElement("div");
  logEntry.textContent = message;
  logDiv.appendChild(logEntry);
}
