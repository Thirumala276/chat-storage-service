--liquibase formatted sql
--changeset thirumalaiah.regatti:create-tables

-- 1️⃣ Create sequence for ID generation
CREATE SEQUENCE seq_id_chat_sessions
    START WITH 10001
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

 -- 1️⃣ Create sequence for ID generation
 CREATE SEQUENCE seq_id_chat_messages
     START WITH 10001
     INCREMENT BY 1
     NO MINVALUE
     NO MAXVALUE
     CACHE 1;

  -- 1️⃣ Create sequence for ID generation
  CREATE SEQUENCE seq_id_knowledge_base
      START WITH 10001
      INCREMENT BY 1
      NO MINVALUE
      NO MAXVALUE
      CACHE 1;

-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;


CREATE TABLE chat_sessions (
  id bigint PRIMARY KEY NOT NULL,
  user_id character varying(255) NOT NULL,
  title text NOT NULL,
  favorite boolean DEFAULT false,
  created_at timestamp without time zone,
  modified_at timestamp without time zone
);

CREATE TABLE chat_messages (
  id bigint PRIMARY KEY NOT NULL,
  session_id bigint NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
  sender text NOT NULL,
  content text NOT NULL,
  retrieved_context TEXT,
  created_at timestamp without time zone,
  modified_at timestamp without time zone
);

CREATE TABLE knowledge_base (
    id bigint PRIMARY KEY NOT NULL,
    title TEXT,
    content TEXT,
    embedding vector(1024),
    created_at timestamp without time zone,
    modified_at timestamp without time zone
);


CREATE INDEX idx_messages_session_seq ON chat_messages(session_id);