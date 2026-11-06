# RAG Project

Spring Boot 3 + Spring AI 1.1.8 — система поиска по документам с генерацией ответов (RAG).

## Стек

| Компонент | Технология |
|-----------|-----------|
| Фреймворк | Spring Boot 3.4.5 |
| AI | Spring AI 1.1.8 (Ollama) |
| БД | PostgreSQL 18 + pgvector 0.8.7 |
| Векторы | HNSW-индекс, косинусное расстояние |
| Эмбеддинги | `nomic-embed-text` (768 dim) |
| LLM | `llama3.2:3b` |

## Быстрый старт

### 1. Требования

- Java 21+
- Maven 3.8+
- PostgreSQL 18 с расширением [pgvector](https://github.com/pgvector/pgvector) 0.8.7
- [Ollama](https://ollama.com) с моделями:

```bash
ollama pull nomic-embed-text
ollama pull llama3.2:3b
```

### 2. Настройка БД

```bash
psql -U postgres -c "CREATE DATABASE rag_db;"
psql -U postgres -d rag_db -c "CREATE EXTENSION IF NOT EXISTS vector;"
psql -U postgres -d rag_db -c "CREATE EXTENSION IF NOT EXISTS hstore;"
psql -U postgres -d rag_db -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
```

### 3. Конфигурация

Пароль от БД задаётся переменной окружения (по умолчанию `postgres`):

```powershell
# Windows PowerShell
$env:SPRING_DATASOURCE_PASSWORD = "ваш_пароль"
```

```bash
# Linux / macOS
export SPRING_DATASOURCE_PASSWORD=ваш_пароль
```

### 4. Запуск

```bash
mvn spring-boot:run
```

Приложение стартует на порту **8080**. При запуске автоматически загружается `data/spring-ai-guide.txt`.

## API

| Метод | URL | Описание |
|-------|-----|----------|
| `POST` | `/api/upload` | Загрузка файла (multipart/form-data) |
| `POST` | `/api/search` | Семантический поиск |
| `POST` | `/api/rag` | RAG-запрос (поиск + генерация ответа) |
| `GET` | `/api/documents` | Список загруженных документов |
| `DELETE` | `/api/documents/{id}` | Удаление документа |

### Примеры

```bash
# Загрузка файла
curl -X POST http://localhost:8080/api/upload -F "file=@document.txt"

# Семантический поиск
curl -X POST http://localhost:8080/api/search -H "Content-Type: application/json" -d "{\"query\":\"как вернуть товар\",\"topK\":5}"

# RAG-ответ (поиск + генерация LLM)
curl -X POST http://localhost:8080/api/rag -H "Content-Type: application/json" -d "{\"query\":\"Как вернуть товар?\",\"topK\":3}"

# Список документов
curl http://localhost:8080/api/documents
```

## Архитектура

```
[Документ] → TokenTextSplitter → чанки → VectorStore.add() → pgvector
                                                              ↓
[Вопрос] → VectorStore.similaritySearch() → контекст → ChatClient → ответ
```

- **JPA** управляет таблицами `documents` и `document_chunks` (метаданные, текст)
- **PgVectorStore** управляет таблицей `vector_store` (эмбеддинги `vector(768)`, HNSW-индекс)
- Разделение таблиц исключает конфликты Hibernate и PgVectorStore

## Структура

```
src/main/java/com/example/ragproject/
├── RagProjectApplication.java      # Точка входа
├── DataLoaderRunner.java           # Автозагрузка при старте
├── config/AiConfig.java            # Бин ChatClient
├── controller/RagController.java   # REST API
├── dto/                            # Запросы и ответы
├── entity/                         # Document, DocumentChunk
├── repository/                     # Spring Data JPA
└── service/
    ├── DocumentUploadService.java  # Загрузка, чанкинг, эмбеддинги
    └── RagService.java             # Поиск + генерация ответа
```

## Тесты

```bash
mvn test -Dtest="RagService*Test"
```

Unit-тесты (`RagServiceSearchTest`, `RagServiceRagQueryTest`, `RagServiceEmptyResultTest`) используют Mockito — не требуют БД, Ollama или Spring-контекста.

## Безопасность

⚠️ Пароль от БД никогда не хранится в репозитории — используйте переменную окружения `SPRING_DATASOURCE_PASSWORD`.