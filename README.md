# AnamHub

A simple server for hosting Anam Wallet mini-app modules with automatic manifest validation and unique app ID generation.

## Key Features

- **Domain-Friendly App IDs**: Generates lowercase-only app IDs (com.anam.{16-char-id}) suitable for subdomain usage
- **Automatic Validation**: Validates manifest.json structure and required fields
- **Smart File Storage**: Stores ZIP files with appId naming
- **Icon Management**: Automatically extracts and serves app icons separately
- **RESTful API**: Clean API for listing, downloading mini-apps, and fetching icons

## Setup

### 1. Prerequisites

- Java 17 or higher
- MariaDB 10.5+ or MySQL 8.0+ (default: MariaDB)
- (Optional) SQLite for simple local development

### 2. Database Setup

#### Option 1: MariaDB (Recommended)
```sql
CREATE DATABASE anamhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Option 2: SQLite
No setup required - database file will be created automatically.

### 3. Configure Application

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` with your settings:

- Database connection (MariaDB or SQLite)
- File upload directory path (`file.mini-app.provide.dir`)
- Icon storage directory path (`file.icon.dir`)
- Server port (default: 9090)

### 4. Run server

```bash
# Development
./gradlew bootRun

# Production
./gradlew build
java -jar build/libs/server-0.0.1-SNAPSHOT.jar
```

## API Documentation

### Base URL

```
http://localhost:9090
```

### Response Format

All API responses follow this structure:

```json
{
  "success": boolean,
  "message": "string",
  "data": object | array | null,
  "timestamp": "ISO 8601 datetime",
  "code": integer
}
```

---

### 1. List All Mini-Apps

Get a list of all available mini-apps.

**Request**

```http
GET /miniapps
```

**Success Response (200 OK)**

```json
{
  "success": true,
  "message": "Found 2 mini-apps",
  "data": [
    {
      "appId": "com.anam.x7k9m2p5q8n3r6w4",
      "fileName": "com.anam.x7k9m2p5q8n3r6w4.zip",
      "type": "BLOCKCHAIN",
      "createdAt": "2025-01-11T15:30:45.123+09:00",
      "name": "DeFi Wallet",
      "version": "1.0.0",
      "iconUrl": "/miniapps/com.anam.x7k9m2p5q8n3r6w4/icon"
    },
    {
      "appId": "com.anam.a3b5c7d9e2f4g6h8",
      "fileName": "com.anam.a3b5c7d9e2f4g6h8.zip",
      "type": "BLOCKCHAIN",
      "createdAt": "2025-01-10T10:15:30.456+09:00",
      "name": "NFT Marketplace",
      "version": "2.1.0",
      "iconUrl": "/miniapps/com.anam.a3b5c7d9e2f4g6h8/icon"
    }
  ],
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 200
}
```

**Empty Response (200 OK)**

```json
{
  "success": true,
  "message": "Found 0 mini-apps",
  "data": [],
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 200
}
```

---

### 2. Get Specific Mini-App

Get details of a specific mini-app by ID.

**Request**

```http
GET /miniapps/{appId}
```

**Parameters**

- `appId` (path parameter): Unique app identifier (format: com.anam.{nanoId})

**Success Response (200 OK)**

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": {
    "appId": "com.anam.x7k9m2p5q8n3r6w4",
    "fileName": "com.anam.x7k9m2p5q8n3r6w4.zip",
    "type": "BLOCKCHAIN",
    "createdAt": "2025-01-11T15:30:45.123+09:00",
    "name": "DeFi Wallet",
    "version": "1.0.0",
    "iconUrl": "/miniapps/com.anam.x7k9m2p5q8n3r6w4/icon"
  },
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 200
}
```

**Error Response (404 Not Found)**

```json
{
  "success": false,
  "message": "MiniApp not found with id: com.anam.invalid-id",
  "data": null,
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 404
}
```

---

### 3. Download Mini-App File

Download the ZIP file of a specific mini-app.

**Request**

```http
GET /miniapps/{appId}/download
```

**Parameters**

- `appId` (path parameter): Unique app identifier (format: com.anam.{nanoId})

**Success Response (200 OK)**

```
HTTP/1.1 200 OK
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="defi-wallet.zip"
Content-Length: 1048576

[Binary ZIP file data]
```

**Error Responses**

_Mini-app not found (404)_

```json
{
  "success": false,
  "message": "MiniApp not found with id: com.anam.invalid-id",
  "data": null,
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 404
}
```

_File not found (500)_

```json
{
  "success": false,
  "message": "An unexpected error occurred: File download failed: Could not read file: example.zip",
  "data": null,
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 500
}
```

---

### 4. Get Mini-App Icon

Get the icon image of a specific mini-app.

**Request**

```http
GET /miniapps/{appId}/icon
```

**Parameters**

- `appId` (path parameter): Unique app identifier (format: com.anam.{nanoId})

**Success Response (200 OK)**

```
HTTP/1.1 200 OK
Content-Type: image/png
Cache-Control: public, max-age=86400
Content-Length: 2048

[Binary image data]
```

**Error Responses**

_Mini-app not found (404)_

```json
{
  "success": false,
  "message": "MiniApp not found with id: com.anam.invalid-id",
  "data": null,
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 404
}
```

_Icon not found (500)_

```json
{
  "success": false,
  "message": "An unexpected error occurred: Icon retrieval failed: Could not read icon file",
  "data": null,
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 500
}
```

---

### 5. Upload Page

Web interface for uploading mini-apps.

**Request**

```http
GET /
```

**Response**
HTML upload page with:

- Type selection (BLOCKCHAIN/APP)
- ZIP file upload with drag & drop
- Automatic manifest.json validation
- Auto-generated unique app ID
- Success message includes generated app_id

---

## Mini-App Requirements

### ZIP File Structure

The ZIP file must contain all files in the root level (no wrapper folder):

```
✅ Correct structure:
mini-app.zip
├── manifest.json          # In root
├── app.js
├── app.css
├── assets/
│   └── icons/
│       └── app_icon.png
└── pages/
    ├── index/
    │   └── index.html
    └── send/
        └── send.html

❌ Wrong structure (with wrapper folder):
mini-app.zip
└── my-app/               # No wrapper folder!
    ├── manifest.json
    └── ...
```

### manifest.json Structure

All mini-apps must include a `manifest.json` file in the root of the ZIP archive:

```json
{
  "app_id": "com.example.myapp", // Ignored - server generates: com.anam.{nanoId}
  "type": "blockchain", // Ignored - set via upload form
  "name": "My Mini App", // Required: Display name (max 20 chars)
  "version": "1.0.0", // Ignored - server sets to "1.0.0"
  "icon": "assets/icons/icon.png", // Required: Icon file path
  "pages": [
    // Required: Page list (minimum 1 page)
    "pages/index/index", // Required: Must include this exact index page
    "pages/send/send" // Optional: Additional pages
  ],
  "bridge": [
    // Optional: Universal Bridge JavaScript files
    "bridge/dapp-bridge.js", // Optional: Bridge scripts for blockchain integration
    "bridge/wallet-connector.js" // Optional: Additional bridge modules
  ]
}
```

### Validation Rules

1. **ZIP File Structure**:
   - Only .zip files are accepted
   - manifest.json must be in the ZIP root directory (not in a subfolder)
2. **manifest.json Required Fields**:
   - `name`: Display name (max 20 characters)
   - `icon`: Path to icon file
   - `pages`: Array of page paths
3. **manifest.json Optional Fields**:
   - `bridge`: Array of Universal Bridge JavaScript file paths (for blockchain integration)
4. **Auto-generated/Ignored Fields**:
   - `app_id`: Always auto-generated as `com.anam.{16-lowercase-chars}` (domain-friendly)
   - `type`: Set via upload form selection
   - `version`: Always set to "1.0.0"
5. **Icon File**: Must exist at the path specified in manifest.json
6. **Page Files**:
   - The `pages` array must contain at least one page
   - **MUST** include `pages/index/index` as the entry point
   - All pages listed must have corresponding `.html` files in the ZIP
   - Example: `"pages/index/index"` → requires `pages/index/index.html` file
7. **Bridge Files** (Optional):
   - If `bridge` array is specified, all listed JavaScript files must exist in the ZIP
   - Used for Universal Bridge integration in blockchain mini-apps
   - Example: `"bridge/dapp-bridge.js"` → requires `bridge/dapp-bridge.js` file

---

## File Storage Structure

After upload, files are stored as follows:

```
/var/anamhub/
├── miniapp-files/
│   ├── com.anam.x7k9m2p5q8n3r6w4.zip
│   └── com.anam.a3b5c7d9e2f4g6h8.zip
└── icons/
    ├── com.anam.x7k9m2p5q8n3r6w4.png
    └── com.anam.a3b5c7d9e2f4g6h8.jpg
```

## Error Codes

| Code | Description           |
| ---- | --------------------- |
| 200  | Success               |
| 404  | Resource not found    |
| 500  | Internal server error |
