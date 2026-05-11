

https://github.com/user-attachments/assets/5c1f6f91-0300-4a37-9801-5b724502e1a8


# Athlekin 🏋️‍♂️

Athlekin is a modern, full-stack Android workout tracking application designed to provide intelligent insights into physical progression. Built with **Jetpack Compose**, it leverages the **Gemini LLM API** to act as a virtual fitness coach, detecting performance plateaus and offering science-based strategies for improvement.

## 🚀 Key Features

- **Intelligent Tracking**: Log exercises with real-time autofill based aon historical data. Stats like sets, reps, and weight are intelligently suggested to minimize friction during workouts.
- **AI Coach (Gemini Integration)**: Automatically analyzes workout volume trends. When a plateau or regression is detected, the app generates personalized coaching insights using Gemini.
- **Google Calendar Sync**: Identifies 1-hour availability windows in your schedule and ranks them based on your historical workout frequency (preferred days and hours).
- **Offline-First & Cloud-Synced**: Uses **Room** for local caching and **Firebase Firestore** for cross-device synchronization and persistent storage.
- **Model Comparison Lab**: A dedicated benchmarking screen to test various Gemini models (Flash vs. Pro) and refine prompt engineering within the app environment.

---

## 🧠 AI Strategy & Benchmarking

One of the project's primary goals was to evaluate Generative AI performance in a utility-based mobile context.

### Model Choice & Performance
Extensive testing was conducted using a custom "Plateau Model Lab" and Google AI Studio's compare feature:

| Model | Choice Rationale |
| :--- | :--- |
| **Gemini 2.5 Flash Lite** | Extremely fast latency, though slated for discontinuation. |
| **Gemini 3 Flash Lite** | Chose **"Minimal Thinking"** budget to reduce latency from ~20s to ~5s. |
| **Conclusion** | Gemini 3 successfully detects subtle discrepancies (e.g., choosing plateau percentages over raw scores) and provides a more consistent "Fitness Coach" persona. |

### Prompt Engineering
We utilized a structured XML-style prompt to enforce persona consistency and data integrity. The prompt includes **Few-Shot Examples** covering:
1. **Strong Progress (>5%)**: Encouraging safe trajectory.
2. **Soft Plateau (0.1%-4.9%)**: Suggesting intensity/rest variation.
3. **Hard Plateau (0%)**: Suggesting mechanical variety (pauses, stance).
4. **Regression (Negative)**: Emphasizing recovery and form.

### Safety Settings
Tested against harassment and dangerous content filters in AI Studio. Even with "block none" settings, the persona remains professional and constructive, even when processing negative user comments or extreme performance regressions.

---

## 🛠️ Technical Stack

- **UI**: Jetpack Compose (Declarative UI)
- **Architecture**: MVVM with Hilt (Dependency Injection)
- **Local Database**: Room (SQLite)
- **Cloud Infrastructure**: Firebase (Authentication, Firestore), Firebase Emulator
- **AI Engine**: Gemini LLM API (Vertex AI SDK for Firebase)
- **Integrations**: Android Calendar Provider (Google Calendar)

---

## 🗺️ Project context

### Roadmap
1. **Foundation**: Completed Android Basics with Compose and basic architecture.
2. **MVP**: Implemented CRUD for exercises and workouts with local/cloud sync.
3. **Intelligence**: Integrated Gemini for plateau detection and GCal for scheduling.
4. **Benchmarking**: Created the `PlateauTesting` suite for LLM evaluation.
5. **Future**: Accessibility testing, advanced ML for slot ranking, and deployment.

## 🛠️ Getting Started
To test the plateau detection without manual logging:
1. Navigate to the **Tracking** screen.
2. Click the **Seed** button (top right) to populate the history with varying progression data.
3. Use the **Autofill** field to select a seeded exercise (e.g., "Bench Press" for progress or "Squat" for plateau).
4. View the **Coach's Insight** card for AI-generated feedback.
