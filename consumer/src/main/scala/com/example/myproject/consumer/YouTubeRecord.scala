package com.example.myproject.consumer

case class YouTubeRecord(
                          video_id: String,
                          trending_date: String,
                          title: String,
                          channel_title: String,
                          category_id: String,
                          publish_time: String,
                          tags: String,
                          views: Int,
                          likes: Int,
                          dislikes: Int,
                          comment_count: Int,
                          thumbnail_link: String,
                          comments_disabled: String,
                          ratings_disabled: String,
                          video_error_or_removed: String,
                          description: String
                        )

object YouTubeRecord {
  def apply(a: Array[String]): YouTubeRecord =
    YouTubeRecord(
      a(0),
      a(1),
      a(2),
      a(3),
      a(4),
      a(5),
      a(6),
      a(7).toInt,
      a(8).toInt,
      a(9).toInt,
      a(10).toInt,
      a(11),
      a(12),
      a(13),
      a(14),
      a(15)
    )
}