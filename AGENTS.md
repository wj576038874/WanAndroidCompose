# AGENTS.md

## Project Overview
This is an Android project built with:
- Kotlin
- Jetpack Compose
- MVVM architecture
- Hilt for Dependency Injection
- Retrofit + OkHttp for networking
- Generate new files and automatically add git.

The project follows a modular feature-based structure.

---

## Project Structure

app/src/main/java/com/wanandroid/compose/

- bean/                  → Data models
- http/                  → Network setup (Retrofit, OkHttp)
- module/                → Hilt DI modules
- route/                 → Navigation system
- [feature]/             → Feature-based modules
    - api/                 → Retrofit interfaces
    - repository/          → Repository interfaces
        - impl/              → Implementations
    - screen/              → Compose UI
    - viewmodel/           → ViewModels (Hilt injected)
    - state/               → UI state
    - event/               → UI events
    - action/              → User actions
- ui/theme/              → Theme system

---

## Architecture Rules (STRICT)

- Must follow MVVM pattern
- UI layer MUST NOT access network layer directly
- All data access MUST go through Repository
- ViewModel is the single source of truth for UI state
- Use unidirectional data flow (State → Event → Action)

---

## Compose Rules

- UI must be stateless whenever possible
- State must come from ViewModel
- Avoid business logic inside Composable functions
- Use remember only for UI-related state
- Use LaunchedEffect for side effects

---

## ViewModel Rules

- Must be annotated with @HiltViewModel
- Inject dependencies via constructor
- Expose immutable State (StateFlow / UI state)
- Handle user actions and map to state changes

---

## Repository Rules

- Define interface in repository/
- Implementation must be in repository/impl/
- Do not expose Retrofit response directly
- Map API response to domain model if needed

---

## Network Rules

- All APIs must be defined in feature/api/
- Retrofit instance provided via Hilt (AppModule)
- Do NOT create Retrofit manually
- Use suspend functions for API calls

---

## Dependency Injection (Hilt)

- All dependencies must be provided via module/
- Do not manually instantiate dependencies
- Use constructor injection whenever possible

---

## Navigation Rules

- Use route/ system for navigation
- All routes must be defined in RouteNavKey
- Navigation must go through Navigator
- Do not directly use NavController in UI layer

---

## Code Style

- Use Kotlin idiomatic code
- Prefer val over var
- Avoid hardcoded strings
- Use constants for API parameters
- Keep functions small and focused

---

## Constraints (IMPORTANT)

- Do NOT modify AndroidManifest.xml unless explicitly required
- Do NOT introduce new libraries without necessity
- Do NOT break existing architecture
- Do NOT bypass Repository layer

---

## File Modification Rules

- Only modify files related to the task
- Keep diff minimal
- Do not refactor unrelated code
- Preserve existing naming conventions

---

## Output Rules (for AI)

- Only output necessary code changes
- Do not include explanations unless requested
- Follow existing project structure strictly

---

## Preferred Patterns

- StateFlow over LiveData
- Sealed classes for State/Event/Action
- Result wrapping for API responses
- Clear separation of UI / Domain / Data

---

## Anti-Patterns (DO NOT DO)

- ❌ Access API directly from UI
- ❌ Business logic inside Composable
- ❌ Global mutable state
- ❌ Manual dependency creation
- ❌ Mixing multiple architectures
