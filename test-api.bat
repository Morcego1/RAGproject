# =============================================================================
# Тестовые запросы для RAG API
# =============================================================================

# Загрузка файла
echo "=== Загрузка файла ==="
curl -X POST http://localhost:8080/api/upload ^
  -F "file=@src\main\resources\data\spring-ai-guide.txt"

# Поиск
echo.
echo "=== Поиск ==="
curl -X POST http://localhost:8080/api/search ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"Как установить pgvector?\", \"topK\": 5}"

# RAG-запрос
echo.
echo "=== RAG-запрос ==="
curl -X POST http://localhost:8080/api/rag ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"Какие провайдеры LLM поддерживает Spring AI?\", \"topK\": 5}"

# Список документов
echo.
echo "=== Список документов ==="
curl http://localhost:8080/api/documents

# Удаление документа (замените {id} на реальный ID)
# curl -X DELETE http://localhost:8080/api/documents/{id}
