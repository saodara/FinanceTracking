# Dara iOS Swift Starter

This folder contains a native SwiftUI starting point for the Android finance app in this repository.

## What is included

- SwiftUI app entry point
- Dashboard with balance, income, and expense summaries
- Transaction entry with validation
- History list
- Budget and savings goal screens
- Simple analytics view
- Profile/logout flow
- Local JSON persistence through `UserDefaults`

## How to use in Xcode

1. Open Xcode.
2. Create a new iOS App project named `DaraApp`.
3. Choose SwiftUI for the interface and Swift for the language.
4. Drag the files from `ios/DaraApp/DaraApp` into the Xcode project.
5. Set the deployment target to iOS 16 or later.
6. Build and run.

Firebase/Auth/Realtime Database are not wired in this starter yet. The Android app currently uses Firebase; the iOS equivalent should be added through Swift Package Manager using Firebase iOS SDK packages when you are ready to connect real accounts and sync.
