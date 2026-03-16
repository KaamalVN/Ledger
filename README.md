# Ledger

Ledger is a privacy-first, cross-platform personal finance application built with **Kotlin Multiplatform** and **Compose Multiplatform**. It allows you to track expenses, income, and recurring payments with a beautiful, minimalist UI.

## App Screens

| Dashboard | Settings | Transactions | Recurring |
|-----------|----------|--------------|-----------|
| <img src="https://github.com/user-attachments/assets/736a4c21-fec4-45ca-b49b-5f6661dc4be5" width="300" alt="Dashboard"/> | <img src="https://github.com/user-attachments/assets/f59fd7fb-df63-44cd-b4a2-74c92711e132" width="300" alt="Settings"/> | <img src="https://github.com/user-attachments/assets/a41d4542-d0cd-44ee-bf69-54933cda845f" width="300" alt="Transactions"/> | <img src="https://github.com/user-attachments/assets/66bbbb8e-c26e-46ed-bb8e-c2de17d033ac" width="300" alt="Recurring"/> |

## Platforms
- **Android**: Native performance and Material 3 design.
- **Web (PWA)**: Modern WebAssembly (Wasm) target with offline support.
- **Desktop**: JVM-based application for Windows, macOS, and Linux.
- **iOS**: Shared UI via Compose Multiplatform.

## Key Features
- **Effortless Tracking**: Simple interface to log transactions in seconds.
- **Recurring Payments**: Dedicated dashboard for subscriptions and bills.
- **Cloud Sync**: Optional backup and restore via **Google Drive**.
- **Privacy First**: All data is stored locally. No external accounts or ads.
- **Material Expressive UI**: A stunning, responsive design across all devices.

## Setup Instructions

### Google Drive Integration (Optional)
To enable Google Drive backup, you need to configure your own API credentials:

1.  **Android**: 
    - Download your `google-services.json` from the Firebase Console.
    - Place it in the `androidApp/` directory.
2.  **Web (PWA)**:
    - Go to the [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
    - Create an **OAuth client ID** (Web application).
    - Add your hosted URL to **Authorized JavaScript origins**.
    - Replace the placeholder ID in `GoogleAuthProvider.kt` (Wasm target) with your new Client ID.

### Running the App

#### Android
```bash
./gradlew :composeApp:assembleDebug
```

#### Web (PWA)
```bash
./gradlew :composeApp:wasmJsBrowserDistribution
```
The output will be in `composeApp/build/dist/wasmJs/productionExecutable`.

#### Desktop
```bash
./gradlew :composeApp:run
```

#### iOS
Open the `iosApp` directory in Xcode or use the Gradle task:
```bash
./gradlew :composeApp:iosSimulatorArm64Run
```

## Project Structure
- `composeApp/commonMain`: Shared logic and UI (Compose).
- `composeApp/androidMain`: Android-specific implementations.
- `composeApp/wasmJsMain`: Kotlin/Wasm logic for the web.
- `composeApp/desktopMain`: JVM-specific logic for Desktop.
- `composeApp/iosMain`: iOS-specific implementations.

---
Built with ❤️ using [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
