# ING Trading

## Backend

### Stack technologiczny
- **Java 17+**, **Spring Boot 4.x**
- **PostgreSQL** + **Flyway** (migracje schematu)
- **Apache Kafka** (powiadomienia email)
- **Caffeine Cache** (cache cen 15 min)
- **WireMock** (symulacja GPW)
- **REST API (/api/*)** — komunikacja z frontendem
- **Docker compose** — uruchomienie środowiska (Kafka, PostgreSQL, WireMock)

### Krótki opis rozwiązań

1. **Interfejs `ExchangeClient`** — systemy giełdowe. Aktualnie jedna implementacja (`GpwClient`), ale łatwo dodać kolejne giełdy przez nową implementację interfejsu.

2. **Cache cen (Caffeine, 15 min TTL)** — wymaganie mówi, że GPW aktualizuje ceny co 15 minut. Cache eliminuje niepotrzebne zapytania HTTP i zwiększa wydajność przy dużym ruchu.

3. **Polling statusu zleceń (`@Scheduled`)** — co 5 sekund sprawdza zlecenia o statusie `Submitted` w GPW. Przy zmianie statusu na `Filled` → aktualizacja DB + powiadomienie Kafka. Przy `Expired` → aktualizacja DB.

4. **Kafka (powiadomienia)** — po zrealizowaniu zlecenia producent wysyła `EmailNotification` na topic `notifications.email.v1`. Consumer odbiera i symuluje wysyłkę maila.

5. **Prowizja** — obliczana dynamicznie na podstawie pola `mic`:
    - GPW (`XWAR`): max(5 PLN, 0.3% wartości transakcji)
    - Rynek zagraniczny: max(10 PLN, 0.2% wartości transakcji)

### Schemat bazy danych
Migracja Flyway automatyczna z katalogu [db.migration](../src/main/resources/db/migration).

### API

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/tickers` | Lista instrumentów |
| GET | `/api/prices` | Aktualne ceny (cache 15 min) |
| POST | `/api/orders` | Złożenie zlecenia kupna |
| GET | `/api/orders` | Lista zleceń użytkownika |
| GET | `/api/orders/{orderId}` | Szczegóły zlecenia (z prowizją) |

### Wiremock
Wiremock jest dostępny z GUI pod linkiem http://localhost:8080/__admin/webapp/mappings

## Frontend

### Ekrany
1. **Kupno papieru** (`buy-order-view.js`) — wyszukiwanie instrumentu, formularz z walidacją, aktualna cena
2. **Lista zleceń** (`order-list-view.js`) — tabela z auto-refreshem co 10s, link do szczegółów tylko dla statusu Filled
3. **Szczegóły zlecenia** (`order-details-view.js`) — pełne dane + prowizja + wartość zlecenia

