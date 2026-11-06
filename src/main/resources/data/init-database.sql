-- =============================================================================
-- Инициализация базы данных для RAG Project
-- =============================================================================

-- 1. Создание базы данных
-- psql -U postgres -c "CREATE DATABASE rag_db;"

-- 2. Подключение к базе
-- \c rag_db

-- 3. Включение расширений PostgreSQL
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 4. Проверка расширений
-- SELECT extname, extversion FROM pg_extension;

-- =============================================================================
-- Примечание
-- =============================================================================
-- Таблицы documents и document_chunks создаются автоматически Hibernate
-- (jpa.hibernate.ddl-auto: update).
--
-- Таблица vector_store создаётся автоматически PgVectorStore
-- (ai.vectorstore.pgvector.initialize-schema: true).
--
-- Этот SQL-файл нужен только для включения расширений.
-- =============================================================================