DROP
DATABASE IF EXISTS youtube;

CREATE
DATABASE youtube;

CREATE SCHEMA stream;
CREATE TABLE if not exists stream.top_tags
(
    tags       varchar,
    count      int,
    created_at timestamp with time zone
);

alter table stream.top_tags
    owner to postgres;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA stream TO postgres;
