let url;
let fileSize = 0;
let chunkSize = 1024 * 1024; // 1 MB chunks
let downloadedChunks = [];
let isPaused = false;

self.onmessage = async (event) => {
  const { action, fileUrl } = event.data;

  if (action === "start") {
    url = fileUrl;
    isPaused = false;
    try {
      fileSize = await getFileSize();
      if (!fileSize) {
        throw new Error(
          "Unable to determine file size. Proceeding without size might not work for large files."
        );
      }
      await startDownload();
    } catch (error) {
      self.postMessage({ status: "error", error: error.message });
    }
  } else if (action === "pause") {
    isPaused = true;
  } else if (action === "resume") {
    isPaused = false;
    await startDownload();
  }
};

// Get file size using HEAD request
async function getFileSize() {
  try {
    const response = await fetch(url, { method: "HEAD" });
    if (!response.ok) {
      throw new Error(`HEAD request failed with status: ${response.status}`);
    }
    const length = response.headers.get("Content-Length");
    if (!length) {
      throw new Error("Content-Length header is missing.");
    }
    return parseInt(length, 10);
  } catch (error) {
    throw new Error(`Failed to fetch file size: ${error.message}`);
  }
}

// Download file chunks
async function startDownload() {
  let downloadedBytes = downloadedChunks.reduce(
    (acc, chunk) => acc + chunk.byteLength,
    0
  );

  while (downloadedBytes < fileSize && !isPaused) {
    const start = downloadedBytes;
    const end = Math.min(downloadedBytes + chunkSize - 1, fileSize - 1);

    try {
      const chunk = await downloadChunk(start, end);
      downloadedChunks.push(chunk);
      downloadedBytes += chunk.byteLength;

      const progress = Math.round((downloadedBytes / fileSize) * 100);
      self.postMessage({ status: "progress", progress });
    } catch (error) {
      self.postMessage({ status: "error", error: error.message });
      return;
    }
  }

  if (downloadedBytes === fileSize) {
    saveFile();
  }
}

// Fetch specific byte range
async function downloadChunk(start, end) {
  const response = await fetch(url, {
    headers: {
      Range: `bytes=${start}-${end}`,
    },
  });
  if (!response.ok) {
    throw new Error(
      `Failed to fetch chunk ${start}-${end}: Status ${response.status}`
    );
  }
  return await response.arrayBuffer();
}

// Save the file
function saveFile() {
  const blob = new Blob(downloadedChunks);
  const fileName = url.split("/").pop() || "download";
  self.postMessage({ status: "complete", file: blob, fileName });
}
