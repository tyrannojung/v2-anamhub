# AnamHub

A simple server for hosting Anam Wallet mini-app modules with automatic manifest validation and unique app ID generation.

## Setup

### 1. Copy configuration template

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 2. Update configuration

Edit `application.properties` with your settings:

- File upload directory path
- Database location (SQLite)
- Server port (default: 9090)

### 3. Run server

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
      "appId": "com.anam.V1StGXR8_Z5jdHi6B",
      "fileName": "defi-wallet.zip",
      "type": "BLOCKCHAIN",
      "createdAt": "2025-01-11T15:30:45.123+09:00",
      "name": "DeFi Wallet",
      "version": "1.0.0"
    },
    {
      "appId": "com.anam.9D3_bLmN7xK4Hf2Qw",
      "fileName": "nft-marketplace.zip",
      "type": "BLOCKCHAIN",
      "createdAt": "2025-01-10T10:15:30.456+09:00",
      "name": "NFT Marketplace",
      "version": "2.1.0"
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
    "appId": "com.anam.V1StGXR8_Z5jdHi6B",
    "fileName": "defi-wallet.zip",
    "type": "BLOCKCHAIN",
    "createdAt": "2025-01-11T15:30:45.123+09:00",
    "name": "DeFi Wallet",
    "version": "1.0.0"
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

### 4. Upload Page

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

---

## Mini-App Requirements

### manifest.json Structure

All mini-apps must include a `manifest.json` file in the root of the ZIP archive:

```json
{
  "app_id": "com.example.myapp",  // Will be replaced with auto-generated ID
  "type": "blockchain",            // Must match selected upload type
  "name": "My Mini App",           // Required: Display name
  "version": "1.0.0",              // Required: Version string
  "icon": "assets/icons/icon.png", // Required: Icon path
  "pages": [                       // Required: Page list (minimum 1 page)
    "pages/index/index",          // Required: Must include this exact index page
    "pages/send/send"             // Optional: Additional pages
  ]
}
```

### Validation Rules

1. **ZIP File**: Only .zip files are accepted
2. **manifest.json**: Must exist in root directory
3. **Required Fields**: name, version, type, icon, pages
4. **Type Matching**: manifest type must match selected upload type
5. **App ID**: Automatically generated as `com.anam.{nanoId}`
6. **Icon File**: Must exist at the path specified in manifest.json
7. **Page Files**: 
   - The `pages` array must contain at least one page
   - **MUST** include `pages/index/index` as the entry point
   - All pages listed must have corresponding `.html` files in the ZIP
   - Example: `"pages/index/index"` → requires `pages/index/index.html` file

---

## Error Codes

| Code | Description           |
| ---- | --------------------- |
| 200  | Success               |
| 404  | Resource not found    |
| 500  | Internal server error |
