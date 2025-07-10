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
- HTTPS settings (optional)

### 3. Run server
```bash
# Build
./gradlew build

# Run
java -jar build/libs/server-0.0.1-SNAPSHOT.jar

# Or directly with Gradle
./gradlew bootRun
```

## API Endpoints
- `GET /list` - List all mini-apps
- `GET /list/{appId}` - Get specific mini-app
- `POST /upload` - Upload mini-app
- `GET /download/{fileName}` - Download mini-app