./# Mini-Jira Workflow Manager

**Mini-Jira** to projekt demonstracyjny stworzony w Kotlinie, który implementuje uproszczony system zarządzania zadaniami (workflow). Głównym celem projektu jest pokazanie praktycznego zastosowania **klasycznych wzorców projektowych** (GoF) w nowoczesnym środowisku **Compose for Desktop**.

System pozwala na tworzenie zadań, zarządzanie ich cyklem życia zgodnie z określonym procesem (TODO -> Done), cofanie błędnych operacji oraz śledzenie zmian w czasie rzeczywistym.

## 🚀 Kluczowe Funkcjonalności
- **Visual Kanban Board**: Zarządzanie zadaniami w podziale na kolumny stanów.
- **Strict Workflow**: Logika przejść między stanami oparta na wzorcu State (brak możliwości przeskoczenia z TODO bezpośrednio do DONE).
- **Undo System**: Możliwość cofnięcia ostatniej zmiany stanu dzięki historii pamiątek (Memento).
- **Live Event Log**: Reagowanie na zmiany w systemie i logowanie ich w UI za pomocą wzorca Observer.
- **Desktop UI**: Nowoczesny interfejs graficzny napisany w Compose for Desktop.

## 🏗️ Architektura i Wzorce Projektowe

Projekt został zaprojektowany z naciskiem na "Clean Code" i dekompozycję odpowiedzialności:

### 1. State (Stan)
Zarządza cyklem życia zadania. Każdy stan (`Todo`, `InProgress`, `Review`, `Done`) jest osobną klasą implementującą interfejs `TaskState`. Dzięki temu logika biznesowa przejść jest odizolowana od klasy zadania i nie wymaga instrukcji warunkowych `if/else` czy `when`.

### 2. Memento (Pamiątka)
Implementuje mechanizm **Undo**. Przed każdą zmianą, zadanie tworzy `TaskMemento` – snapshot swojego stanu. `TaskHistory` przechowuje te snapshoty na stosie, umożliwiając przywrócenie poprzedniego stanu obiektu.

### 3. Observer (Obserwator)
Umożliwia luźne powiązanie (loose coupling) pomiędzy domeną a systemem powiadomień/logowania. UI oraz Logger subskrybują zdarzenia w zadaniu, automatycznie aktualizując widok lub listę logów po każdej zmianie.

### 4. Iterator
Wykorzystywany do bezpiecznego przeglądania tablicy zadań (`TaskBoard`) i filtrowania ich według statusów bez ujawniania wewnętrznej struktury kolekcji repozytorium.

### 5. Adapter / Repository
Zastosowanie warstwy abstrakcji dla danych. Obecnie system korzysta z `InMemoryTaskRepository`, ale architektura pozwala na łatwe podpięcie bazy danych lub API dzięki ujednoliconemu interfejsowi.

## 🛠️ Technologie
- **Język:** Kotlin 1.9+
- **UI Framework:** Compose for Desktop (Jetpack Compose)
- **Zarządzanie projektem:** Gradle (Kotlin DSL)

## 📁 Struktura projektu
```text
src/main/kotlin/
 ├─ domain/
 │   ├─ task/        # Logika zadania i encje
 │   ├─ states/      # Implementacja wzorca State
 │   ├─ memento/     # System Undo (Memento)
 │   └─ observer/    # Interfejsy i implementacje obserwatorów
 ├─ repository/      # Warstwa danych (Adapter/Repo)
 └─ ui/              # Komponenty Compose Desktop
```

## 🚦 Jak uruchomić?
1. Sklonuj repozytorium.
2. Upewnij się, że masz zainstalowane JDK 17 lub nowsze.
3. Uruchom projekt za pomocą Gradle:
   ```bash
   ./gradlew run
   ```

## 📝 Scenariusz demonstracyjny
1. Kliknij **"Add Task"**, aby utworzyć nowe zadanie.
2. Użyj **ikony strzałki** na karcie zadania, aby przesunąć je do kolejnego etapu (np. z TODO do IN PROGRESS).
3. Zaobserwuj, jak w dolnym panelu **Event Logs** pojawiają się wpisy generowane przez `TaskObserver`.
4. Kliknij **ikonę cofania (Undo)**, aby przywrócić zadanie do poprzedniego stanu.

---
*Projekt ma charakter edukacyjny i służy jako prezentacja umiejętności projektowania architektury obiektowej.*