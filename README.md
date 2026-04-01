# 🚀 CollabXSkill

**CollabXSkill** is a developer collaboration platform that helps developers find the *right* tech partners based on **skills, experience, and intent** — not random connections.

It is built as a **production-grade backend system** with real-time communication, secure authentication, and intelligent matching.

---

## 🧠 Problem

Developers often struggle to:

* Find the right collaborators for projects
* Get help in specific tech stacks (e.g., Spring Boot, React)
* Avoid noisy and unstructured platforms

Most existing platforms are:

* Too formal (professional-only networking)
* One-sided (no mutual interaction system)
* Random (no intelligent matching)

---

## 💡 Solution

CollabXSkill introduces a **skill-based matching engine** combined with a **mutual interaction system**, enabling developers to:

* Discover relevant collaborators
* Express intent clearly
* Start real conversations only when both sides are interested

---

## ⚙️ Core Features

### ⚡ Weighted Matching Algorithm

Each developer profile is ranked using:

* Same domain → +40 pts
* Common tech stack → +10 per match
* Same experience → +20 pts

👉 Ensures highly relevant matches
👉 Eliminates random discovery

---

### 🔥 SUPER COLLAB (Priority Requests)

* Send high-priority collaboration requests

* Example:

  > “48hr hackathon — need a Spring Boot dev”

* Limited to **3/day**

* Always appears at the top

---

### 🤝 Silent Matching System

* Mutual interest → chat unlocks automatically
* No cold DMs or spam

---

### 🔒 Granular Interaction Controls

* Temporary rejection (7 days)
* Permanent block
* Designed with a human-first approach:

  > *Not every "no" is forever*

---

### 💬 Real-Time Chat System

* WebSocket + STOMP integration
* Secure messaging between matched users

---

## 🛡️ Security & Authentication

* JWT Authentication
* Refresh Token Rotation
* Token Versioning (force logout on password change)
* Secure WebSocket handshake using JWT
* Rate limiting on sensitive endpoints

---

## ⚙️ Backend Engineering Highlights

* Clean layered architecture (Controller → Service → Repository)
* DTO-based request/response handling
* Global exception handling
* Centralized constants & enums
* Pagination for scalable APIs
* Data initialization for testing

---

## 📡 API Documentation

API documentation (Swagger/OpenAPI) can be integrated for:

* Endpoint testing
* Request/response validation
* Authentication flows

---

## 🛠️ Tech Stack

* **Backend:** Java, Spring Boot
* **Database:** PostgreSQL
* **Authentication:** JWT
* **Realtime:** WebSocket + STOMP
* **DevOps:** Docker

---

## 📁 Project Structure

```bash id="struct1"
collabxskill/
├── Configuration/
│   ├── ModelMapperConfig.java
│   └── WebSocketConfig.java
├── Contoller/
│   ├── ChatController.java
│   ├── ChatRestController.java
│   ├── UserActionController.java
│   ├── UserController.java
│   └── UserProfileController.java
├── Entities/
│   ├── AppSettings.java
│   ├── ChatMessage.java
│   ├── RefreshToken.java
│   ├── Skill.java
│   ├── User.java
│   ├── UserAction.java
│   ├── UserProfile.java
│   └── UserSkill.java
├── Service/
│   ├── ServiceImpl/
│   │   ├── AppSettingsServiceImpl.java
│   │   ├── ChatServiceImpl.java
│   │   ├── EmailServiceImpl.java
│   │   ├── UserActionServiceImpl.java
│   │   ├── UserProfileServiceImpl.java
│   │   └── UserServiceImpl.java
│   ├── AppSettingsService.java
│   ├── ChatService.java
│   ├── EmailService.java
│   ├── UserActionService.java
│   ├── UserProfileService.java
│   └── UserService.java
├── extra/
│   ├── ActionType.java
│   ├── Constants.java
│   ├── DataInitializer.java
│   ├── ExperienceLevel.java
│   ├── Gender.java
│   ├── GlobalExceptionHandler.java
│   ├── PrimaryDomain.java
│   └── WebConfig.java
├── io/
│   ├── ChatMessageDTO.java
│   ├── CollabReceivedDTO.java
│   ├── SkillDTO.java
│   ├── UserProfileDTO.java
│   ├── UserRequestDTO.java
│   └── UserResponseDTO.java
├── repo/
│   ├── AppSettingsRepository.java
│   ├── ChatRepo.java
│   ├── RefreshRepo.java
│   ├── UserActionRepository.java
│   ├── UserProfileRepo.java
│   └── UserRepository.java
├── security/
│   ├── AuthCookieUtil.java
│   ├── JwtAuthFilter.java
│   ├── JwtHandshakeInterceptor.java
│   ├── JwtUtil.java
│   ├── SecurityConfig.java
│   └── SecurityUtil.java
└── CollabXSkillApplication.java


```

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Maven
* PostgreSQL

### Setup


git clone (https://github.com/Aryanyadav99/CollabXIQ.git)

cd collabxskill

./mvnw spring-boot:run

```
## 🧾 Commit Style

Following clean and structured commit conventions:

* `feat:` for new features
* `fix:` for bug fixes
* `refactor:` for code improvements

---

## 🚧 Future Enhancements

* Cloudinary integration (media upload)
* AWS deployment (scalable infra)
* React frontend
* Premium tier (advanced collaboration tools)

---

## 🎯 Vision

To build a platform where developers don’t just connect —
they **find, match, and build together efficiently**.

---

## 🤝 Contribution

Contributions are welcome.
Please maintain:

* Clean commit messages
* Proper API documentation
* Code consistency

---

## ⭐ Support

If you like this project, consider giving it a ⭐ on GitHub.
