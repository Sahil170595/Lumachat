# 💬 LumaChat – AI-Powered JavaFX Messaging App

LumaChat is a modern, AI-enhanced desktop chat application built using JavaFX and MongoDB. It features smart assistant integration via **Gemma (local)** and **Gemini (Google Cloud)**, real-time messaging, friend management, and AI command parsing — all in a stylish dark-themed UI.

---

## ✨ Features

- ✅ User registration & login with MongoDB backend
- ✅ Chat system with friend list, timestamps, and history
- ✅ Built-in **AI Assistant (Gemma via Ollama)**
- ✅ **Gemini Assistant**: triggered using `@Gemini`, `#AI`, or smart commands
- ✅ Inline AI responses with styled message bubbles
- ✅ Smart command parsing: `#summarize`, `#translate`, `#explain`, etc.
- ✅ Profile view, secure credential storage
- ✅ Modular and extensible backend (MongoDB + Java Services)

---

## 📸 Screenshots

> Login, registration, chat with Gemini, styled assistant bubbles, and friend list.

Login:
![image](https://github.com/user-attachments/assets/e0f84150-ee85-40b9-910f-825705a4e583)

Chat with LocalAI:
![image](https://github.com/user-attachments/assets/3d260531-5d98-4424-85a1-8e6b3c06091b)

#Chat with Gemini:
![image](https://github.com/user-attachments/assets/1ae9ab79-503b-47bc-99bf-da84802d8334)

---

## ⚙️ Technologies Used

- **JavaFX 21**
- **MongoDB Java Driver**
- **Ollama + Gemma 3B**
- **Google Gemini API**
- **Maven**
- **FXML for UI**
- **Custom CSS styling**

---

## 🚀 Run Locally

### Prerequisites
- Java 17+
- Maven
- MongoDB (local or Atlas)
- [Ollama](https://ollama.com) for local AI
- Gemini API key (if enabled)

### Steps

```bash
# Clone the repo
git clone https://github.com/Sahil170595/Lumachat.git
cd Lumachat

# Set Gemini API key (if using Gemini)
export GEMINI_API_KEY=your_key_here  # or set in environment variables

# Run the app
mvn clean javafx:run
