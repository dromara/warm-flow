-- ============================================================
-- PostgreSQL database/user bootstrap for the RuoYi + Warm-Flow
-- Jimmer demo. Run this against the maintenance database first:
--
--   psql "postgresql://postgres@192.168.2.226:5432/postgres" \
--     -v ON_ERROR_STOP=1 \
--     -v app_password='change-me' \
--     -f sql/postgresql/00-create-database.sql
--
-- Then connect to warm_flow_jimmer_demo and run
-- ruoyi-warm-flow-jimmer-postgres.sql.
-- ============================================================

\set app_db warm_flow_jimmer_demo
\set app_user warm_flow_jimmer_demo
\if :{?app_password}
\else
\set app_password warm_flow_jimmer_demo
\endif

SELECT format('CREATE ROLE %I LOGIN PASSWORD %L', :'app_user', :'app_password')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = :'app_user')
\gexec

ALTER ROLE :app_user WITH LOGIN PASSWORD :'app_password';

SELECT format('CREATE DATABASE %I OWNER %I ENCODING %L', :'app_db', :'app_user', 'UTF8')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = :'app_db')
\gexec

GRANT ALL PRIVILEGES ON DATABASE :app_db TO :app_user;
