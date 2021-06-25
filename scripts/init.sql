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

    CREATE TABLE if not exists youtube_records
    (
        video_id varchar,
        trending_date varchar,
        title varchar,
        channel_title varchar,
        category_id varchar,
        publish_time varchar,
        tags varchar,
        views int,
        likes int,
        dislikes int,
        comment_count int,
        thumbnail_link varchar,
        comments_disabled varchar,
        ratings_disabled varchar,
        video_error_or_removed varchar,
        description varchar,
        created_at timestamp with time zone
    );

alter table stream.top_tags
    owner to postgres;

alter table batch.top_tags
    owner to postgres;

GRANT ALL PRIVILEGES ON DATABASE youtube TO postgres;