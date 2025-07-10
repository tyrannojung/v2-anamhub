// ANAM Upload Portal JavaScript

document.addEventListener('DOMContentLoaded', function() {
  const fileInput = document.querySelector('.file-input');
  const uploadArea = document.querySelector('.file-upload-area');
  const uploadText = document.querySelector('.upload-text');

  // File selection
  fileInput.addEventListener('change', function(e) {
    if (this.files && this.files[0]) {
      const fileName = this.files[0].name;
      uploadText.textContent = '📦 ' + fileName;
      uploadArea.classList.add('active');
    }
  });

  // Drag and drop events
  uploadArea.addEventListener('dragover', function(e) {
    e.preventDefault();
    this.classList.add('active');
  });

  uploadArea.addEventListener('dragleave', function(e) {
    e.preventDefault();
    this.classList.remove('active');
  });

  uploadArea.addEventListener('drop', function(e) {
    e.preventDefault();
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      fileInput.files = files;
      const event = new Event('change', { bubbles: true });
      fileInput.dispatchEvent(event);
    }
  });
});