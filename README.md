
# Google Meet Integration App

This Android application allows users to authenticate using Google Sign-In and easily create or join Google Meet meetings with a single click. Built using **Kotlin**, **Jetpack Compose**, and following the **MVVM** architecture, this app integrates the **Google Meet API** to streamline the process of creating and joining meetings.

## Features
- **Google Sign-In Authentication**: Secure user login using OAuth 2.0 and Google Sign-In API.
- **Create or Join Meet with One Click**: After signing in, users can create or join a Google Meet session with just one click.
- **Google Meet API Integration**: Seamless integration with Google Meet API, allowing automatic meeting creation and access to Google Meet URLs.
- **MVVM Architecture**: Ensures clean separation of concerns and robust data flow management, making the app scalable and maintainable.

## Technologies Used
- **Kotlin**
- **Jetpack Compose** for UI
- **Google Sign-In API** for user authentication
- **Google Meet API** for meeting creation and joining
- **MVVM** architecture for clear code structure and separation of concerns

## How It Works
1. **User Authentication**: Users authenticate via Google Sign-In, securely logging into the app.
2. **Meet Creation/Joining**: Once authenticated, users can create or join Google Meet meetings with a single click, streamlining the process.
3. **Integration**: The app uses Google’s OAuth 2.0 for authentication and accesses Google Meet API to handle meeting operations, including generating meet links.

## Getting Started
To get started with the project:
1. Clone the repository.
2. Set up your Google OAuth 2.0 credentials for Google Sign-In and Meet API integration.
3. Build the project in Android Studio and run it on an Android device or emulator.

## Requirements
- Android Studio (Arctic Fox or above)
- Kotlin 1.5+
- Jetpack Compose 1.0+
- Google OAuth 2.0 Credentials
