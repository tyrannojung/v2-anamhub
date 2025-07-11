# AnamHub

A simple server for hosting Anam Wallet mini-app modules.

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
      "appId": "550e8400-e29b-41d4-a716-446655440000",
      "fileName": "defi-wallet.zip",
      "type": "BLOCKCHAIN",
      "createdAt": "2025-01-11T15:30:45.123+09:00"
    },
    {
      "appId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "fileName": "nft-marketplace.zip",
      "type": "BLOCKCHAIN",
      "createdAt": "2025-01-10T10:15:30.456+09:00"
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

- `appId` (path parameter): UUID of the mini-app

**Success Response (200 OK)**

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": {
    "appId": "550e8400-e29b-41d4-a716-446655440000",
    "fileName": "defi-wallet.zip",
    "type": "BLOCKCHAIN",
    "createdAt": "2025-01-11T15:30:45.123+09:00"
  },
  "timestamp": "2025-01-11T16:45:30.123",
  "code": 200
}
```

**Error Response (404 Not Found)**

```json
{
  "success": false,
  "message": "MiniApp not found with id: 550e8400-invalid-id",
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

- `appId` (path parameter): UUID of the mini-app

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
  "message": "MiniApp not found with id: invalid-id",
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
HTML upload page

---

## Error Codes

| Code | Description           |
| ---- | --------------------- |
| 200  | Success               |
| 404  | Resource not found    |
| 500  | Internal server error |
