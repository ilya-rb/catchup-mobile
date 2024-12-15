package com.illiarb.catchup.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public fun <T, R> Flow<Async<T>>.mapContent(mapper: (T) -> R): Flow<Async<R>> {
  return map { data ->
    when (data) {
      is Async.Loading -> Async.Loading
      is Async.Error -> Async.Error(data.error)
      is Async.Content -> {
        Async.Content(mapper(data.content))
      }
    }
  }
}
