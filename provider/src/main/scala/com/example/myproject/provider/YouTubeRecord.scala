package com.example.myproject.provider

import org.apache.commons.csv.CSVRecord

case class YouTubeRecord(
                          video_id: String,
                          trending_date: String,
                          title: String,
                          channel_title: String,
                          category_id: String,
                          publish_time: String,
                          tags: String,
                          views: String,
                          likes: String,
                          dislikes: String,
                          comment_count: String,
                          thumbnail_link: String,
                          comments_disabled: String,
                          ratings_disabled: String,
                          video_error_or_removed: String,
                          description: String
                        )

object YouTubeRecord {
  def apply(a: CSVRecord): YouTubeRecord =
    YouTubeRecord(
      a.get(0),
      a.get(1),
      a.get(2),
      a.get(3),
      a.get(4),
      a.get(5),
      a.get(6),
      a.get(7),
      a.get(8),
      a.get(9),
      a.get(10),
      a.get(11),
      a.get(12),
      a.get(13),
      a.get(14),
      a.get(15)
    )
}