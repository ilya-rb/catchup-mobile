package com.illiarb.peek.api.domain

import com.illiarb.peek.core.types.Url

public data class NewsSource(
  val kind: Kind,
  val icon: Url,
  val name: String,
) {

  public enum class Kind(public val key: String) {
    IrishTimes(key = "irishtimes"),
    HackerNews(key = "hackernews"),
    Dou(key = "dou");

    public companion object {

      public fun fromKey(key: String): Kind {
        return when (key) {
          IrishTimes.key -> IrishTimes
          HackerNews.key -> HackerNews
          Dou.key -> Dou
          else -> throw IllegalArgumentException("Unknown key: $key")
        }
      }
    }
  }
}
