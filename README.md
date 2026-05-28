# Smart Navigation & Compass Application

A modern Android navigation application developed using Kotlin and Jetpack Compose, designed to provide real-time GPS tracking, intelligent waypoint navigation, and interactive compass-based guidance through lifecycle-aware mobile architecture and sensor integration.

The application combines device location services, orientation sensors, gesture-based interactions, and reactive UI components to deliver an efficient and responsive navigation experience for mobile users.

---

# Overview

This project focuses on the development of a real-time mobile navigation system capable of managing live GPS updates, compass orientation tracking, and waypoint-based directional assistance within a modern Android environment.

The application demonstrates practical implementation of:

* GPS location services
* Device sensor integration
* Real-time state management
* Lifecycle-aware resource handling
* Interactive mobile user interfaces
* Performance-conscious Android architecture

The system was designed with an emphasis on responsiveness, usability, battery efficiency, and scalable mobile application structure.

---

# Key Features

## Real-Time Navigation System

* Live GPS location tracking with continuous location updates
* Dynamic waypoint guidance and automated waypoint switching
* Real-time distance and directional calculations
* Compass-based navigation support for improved orientation awareness

## Interactive Compass Interface

* Custom compass visualisation built using Jetpack Compose Canvas
* Dynamic compass rotation based on device orientation
* Waypoint rendering with directional indicators
* Gesture-based waypoint selection and zoom functionality

## Advanced Sensor Integration

* Real-time orientation sensor processing
* Lifecycle-aware sensor registration and disposal
* Efficient handling of GPS and rotation listeners
* Optimised background resource management

## User Experience & UI

* Reactive user interface developed with Jetpack Compose
* Dynamic waypoint management and selection
* Responsive control layouts and navigation components
* Real-time UI updates based on sensor and location state changes

## Performance Optimisation

* Reduced unnecessary sensor usage through lifecycle-aware management
* Battery-efficient GPS listener handling
* Safe state handling and memory-conscious architecture
* Improved responsiveness through reactive Compose state management

---

# Technologies Used

## Mobile Development

* Kotlin
* Android SDK
* Jetpack Compose

## Location & Sensor Services

* GPS Location Services
* Orientation Sensors
* Rotation Vector Sensors

## Android Architecture & UI

* Compose UI
* State Management
* Lifecycle-aware Components
* Reactive UI Design

---

# Application Architecture

```bash id="d6pjm2"
Device Sensors & GPS Services
              ↓
Lifecycle-aware State Management
              ↓
MainActivity Application Controller
              ↓
Jetpack Compose UI Layer
              ↓
Interactive Compass & Navigation Interface
```

---

# Core Functionalities

## Navigation Management

* GPS-based waypoint navigation
* Automated waypoint progression
* Distance and bearing calculations
* Real-time heading updates

## Compass System

* Dynamic compass rendering
* Waypoint visualisation
* Compass gesture interaction
* Zoomable navigation interface

## Sensor Management

* Orientation sensor handling
* Efficient listener registration
* Real-time directional processing
* Lifecycle-aware resource optimisation

---

# Installation & Setup

## Clone Repository

```bash id="w6g2sq"
git clone https://github.com/Tittuerothu/GPAWAYPOINT1.git
```

## Open Project

```bash id="wslj0e"
Open the project using Android Studio
```

## Build & Run

```bash id="f7r4mc"
Run the application on an Android emulator or physical Android device
```

---

# Future Enhancements

* Google Maps API integration
* Offline waypoint persistence using Room Database
* Route optimisation and navigation history
* Voice-guided navigation assistance
* Dark mode and accessibility improvements
* Cloud synchronisation support

---

# Repository Structure

```bash id="m72pxi"
GPAWAYPOINT1/
│
├── MainActivity.kt
├── Compass.kt
├── SharedComposables.kt
├── DistanceUtils.kt
├── OrientationSensor.kt
├── WaypointStorage.kt
├── Waypoint.kt
└── README.md
```

---

# Author

**Tittu Erothu**
MSc Computing Science
Griffith College Dublin

GitHub: https://github.com/Tittuerothu
