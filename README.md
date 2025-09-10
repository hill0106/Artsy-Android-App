# 🎨 Artsy Android App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-24%2B-green.svg)](https://developer.android.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09.00-orange.svg)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-Artsy-red.svg)](https://www.artsy.net/)

A modern Android application built with Jetpack Compose that allows users to discover, search, and manage their favorite artists using the Artsy API. The app provides a seamless experience for art enthusiasts to explore artist profiles, view artworks, and maintain a personalized favorites collection.

## 📑 Table of Contents

- [📱 Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [📦 Installation & Setup](#-installation--setup)
- [🚀 Getting Started](#-getting-started)
- [📱 Screenshots](#-screenshots)
- [🔌 API Integration](#-api-integration)
- [🧪 Testing](#-testing)
- [📁 Project Structure](#-project-structure)
- [🔧 Configuration](#-configuration)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [🙏 Acknowledgments](#-acknowledgments)
- [📞 Support](#-support)
- [🔮 Future Enhancements](#-future-enhancements)

## 📱 Features

### 🔍 **Artist Discovery**
- **Search Functionality**: Search for artists by name with real-time results
- **Artist Profiles**: Detailed artist information including biography, nationality, and birth year
- **Artwork Gallery**: View artist's artworks with high-quality images
- **Similar Artists**: Discover related artists based on your interests

### ❤️ **Favorites Management**
- **Personal Collection**: Save and organize your favorite artists
- **Real-time Updates**: Live timestamps showing when artists were added
- **Quick Access**: Easy navigation to favorite artist profiles
- **Persistent Storage**: Favorites are saved and synchronized across sessions

### 👤 **User Authentication**
- **Secure Login**: Email and password authentication
- **User Registration**: Create new accounts with profile management
- **Session Management**: Persistent login with automatic session validation
- **Account Management**: Logout and account deletion options

### 🎨 **Modern UI/UX**
- **Material Design 3**: Latest Material Design components and theming
- **Jetpack Compose**: Modern declarative UI framework
- **Responsive Design**: Optimized for various screen sizes
- **Dark/Light Theme**: Adaptive theming support
- **Smooth Animations**: Fluid transitions and micro-interactions

## 🏗️ Architecture

### **MVVM Pattern**
The app follows the Model-View-ViewModel architecture pattern for clean separation of concerns:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel       │    │  Data Layer     │
│   (Compose)     │◄──►│  (State Mgmt)    │◄──►│  (API/Models)   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### **Key Components**
- **UI Screens**: Jetpack Compose-based screens for each feature
- **Navigation**: Type-safe navigation with Compose Navigation
- **State Management**: Reactive state management with Compose state
- **API Integration**: Retrofit-based REST API client
- **Dependency Injection**: Koin for lightweight DI

## 🛠️ Tech Stack

### **Core Technologies**
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Compose
- **Navigation**: Navigation Compose 2.8.9
- **Dependency Injection**: Koin 3.3.3

### **Networking & Data**
- **HTTP Client**: Retrofit 2.9.0
- **JSON Serialization**: Kotlinx Serialization 1.6.3
- **Image Loading**: Coil 2.6.0
- **Cookie Management**: PersistentCookieJar
- **Logging**: OkHttp Logging Interceptor

### **Android Libraries**
- **Core**: AndroidX Core KTX 1.16.0
- **Lifecycle**: Lifecycle Runtime KTX 2.8.7
- **Material Design**: Material 3 1.1.0
- **Coroutines**: Kotlinx Coroutines Android 1.7.3

## 📦 Installation & Setup

### **Prerequisites**
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 24+ (minimum), 35 (target)
- Git

### **Clone the Repository**
```bash
git clone https://github.com/yourusername/artsy-android-app.git
cd artsy-android-app
```

### **Build Configuration**
1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Ensure all dependencies are resolved
4. Build the project (Ctrl+F9 / Cmd+F9)

### **Run the Application**
1. Connect an Android device or start an emulator
2. Click the "Run" button in Android Studio
3. The app will install and launch automatically

## 🚀 Getting Started

### **First Launch**
1. **Welcome Screen**: The app opens to the home screen showing today's date
2. **Authentication**: Tap the profile icon to login or register
3. **Search Artists**: Use the search icon to discover new artists
4. **Add Favorites**: Like artists to add them to your favorites collection

### **Basic Usage**
1. **Search**: Enter artist names in the search bar
2. **Explore**: Tap on artist cards to view detailed profiles
3. **Favorite**: Use the heart icon to add artists to favorites
4. **Manage**: Access your favorites from the home screen

## 📱 Screenshots

> **Note**: Screenshots will be added here showing the app's key screens:
> - Home screen with favorites
> - Search functionality
> - Artist detail view
> - Login/Register screens
> - User profile management

## 🔌 API Integration

### **Artsy API Endpoints**
The app integrates with the Artsy API for artist data:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/search` | GET | Search for artists by query |
| `/artist/{id}` | GET | Get detailed artist information |
| `/artwork/{id}` | GET | Get artwork details |
| `/gene/{id}` | GET | Get genre/category information |
| `/artist/smlr/{id}` | GET | Get similar artists |

### **Authentication Endpoints**
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/auth/login` | POST | User login |
| `/user/signup` | POST | User registration |
| `/auth/logout` | POST | User logout |
| `/user/me` | GET | Get current user data |
| `/user/liked/{id}` | POST | Like an artist |
| `/user/rmliked/{id}` | DELETE | Remove artist from favorites |
| `/user` | DELETE | Delete user account |

### **Data Models**
```kotlin
// Core artist data structure
data class Artist(
    val id: String,
    val name: String,
    val nationality: String?,
    val birthday: String?,
    val deathday: String?,
    val biography: String?,
    val imageUrl: String?
)

// User favorites
data class Favorite(
    val artistId: String,
    val name: String,
    val nationality: String?,
    val birthday: String?,
    val likedAt: String?
)
```

## 🧪 Testing

### **Running Tests**
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

### **Test Coverage**
- Unit tests for data models and API services
- UI tests for critical user flows
- Integration tests for API endpoints

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/hw4/
│   │   ├── data/
│   │   │   ├── api/           # API client and services
│   │   │   └── model/         # Data models and DTOs
│   │   ├── ui/
│   │   │   ├── theme/         # Material Design theming
│   │   │   ├── HomeScreen.kt  # Main favorites screen
│   │   │   ├── SearchScreen.kt # Artist search
│   │   │   ├── ArtistScreen.kt # Artist details
│   │   │   ├── LoginScreen.kt  # Authentication
│   │   │   └── RegisterScreen.kt # User registration
│   │   └── MainActivity.kt    # Main activity and navigation
│   └── res/                   # Resources (layouts, strings, etc.)
├── build.gradle.kts           # App-level build configuration
└── proguard-rules.pro         # ProGuard configuration
```

## 🔧 Configuration

### **Build Variants**
- **Debug**: Development build with logging enabled
- **Release**: Production build with optimizations

### **Environment Variables**
The app uses the following configuration:
- **API Base URL**: Configured in `ApiClient.kt`
- **Network Timeout**: 30 seconds
- **Image Cache**: 50MB disk cache

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### **Development Workflow**
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### **Code Style**
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Ensure all tests pass

### **Pull Request Guidelines**
- Provide a clear description of changes
- Include screenshots for UI changes
- Update documentation as needed
- Ensure CI/CD checks pass

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Artsy**: For providing the comprehensive art API
- **Google**: For Jetpack Compose and Material Design
- **Open Source Community**: For the amazing libraries that made this possible

## 📞 Support

If you encounter any issues or have questions:

1. **Check the Issues**: Look through existing GitHub issues
2. **Create an Issue**: Provide detailed information about the problem
3. **Contact**: Reach out to the maintainers

## 🔮 Future Enhancements

### **Planned Features**
- [ ] **Offline Support**: Cache artist data for offline viewing
- [ ] **Push Notifications**: Notify users about new artworks from favorite artists
- [ ] **Social Features**: Share favorite artists with friends
- [ ] **Advanced Search**: Filter by nationality, birth year, art movements
- [ ] **Artwork Collections**: Create custom collections of artworks
- [ ] **Dark Mode**: Enhanced dark theme support
- [ ] **Accessibility**: Improved accessibility features
- [ ] **Performance**: Image optimization and lazy loading

### **Technical Improvements**
- [ ] **Database**: Local Room database for offline storage
- [ ] **Caching**: Advanced caching strategies
- [ ] **Analytics**: User behavior tracking
- [ ] **CI/CD**: Automated testing and deployment
- [ ] **Security**: Enhanced authentication and data protection

---

**Built with ❤️ using Jetpack Compose and the Artsy API**

*Last updated: December 2024*