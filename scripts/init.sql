DROP
    DATABASE IF EXISTS youtube;

CREATE
    DATABASE youtube;

\c youtube

CREATE SCHEMA stream

    CREATE TABLE if not exists top_tags
    (
        tag        varchar,
        count      int,
        created_at timestamp with time zone
    );

CREATE SCHEMA batch

    CREATE TABLE if not exists top_tags
    (
        tag        varchar,
        count      int,
        created_at timestamp with time zone
    );

alter table stream.top_tags
    owner to postgres;

alter table batch.top_tags
    owner to postgres;

GRANT ALL PRIVILEGES ON DATABASE youtube TO postgres;