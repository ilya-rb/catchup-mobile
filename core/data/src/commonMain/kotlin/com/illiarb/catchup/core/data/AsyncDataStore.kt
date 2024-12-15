package com.illiarb.catchup.core.data

import com.illiarb.catchup.core.coroutines.suspendRunCatching
import com.illiarb.catchup.core.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

public class AsyncDataStore<Params, Domain>(
  private val networkFetcher: suspend (Params) -> Domain,
  private val fromStorage: suspend (Params) -> Domain?,
  private val intoStorage: suspend (Params, Domain) -> Unit,
  private val fromMemory: (Params) -> Domain?,
  private val intoMemory: (Params, Domain) -> Unit,
) {

  /**
   * TODO:
   *  1. Shared requests with the same params
   *  2. Force reload and time based cached strategy
   *  3. Async background reload method
   */
  public fun collect(params: Params, strategy: LoadStrategy): Flow<Async<Domain>> = flow {
    val memCached = suspendRunCatching { fromMemory.invoke(params) }
      .onFailure { error ->
        Logger.e(TAG, error) { "Failed to read from memory cache, skipping.." }
      }
      .getOrNull()

    if (memCached != null) {
      emit(Async.Content(memCached))
    }

    val storageCached = if (memCached != null) {
      null
    } else {
      emit(Async.Loading)

      suspendRunCatching { fromStorage.invoke(params) }
        .onFailure { error ->
          Logger.e(TAG, error) { "Failed to read from storage, skipping.." }
        }
        .onSuccess { fromStorage ->
          if (fromStorage != null) {
            suspendRunCatching { intoMemory.invoke(params, fromStorage) }.onFailure { error ->
              Logger.e(TAG, error) { "Failed to store warmup memory cache" }
            }
          }
        }
        .getOrNull()
    }

    if (storageCached != null) {
      emit(Async.Content(storageCached))
    }

    val fromNetwork = when (strategy) {
      LoadStrategy.CacheFirst -> if (memCached == null) {
        suspendRunCatching {
          networkFetcher.invoke(params)
        }
      } else {
        null
      }

      LoadStrategy.CacheOnly -> {
        if (memCached == null && storageCached == null) {
          suspendRunCatching {
            networkFetcher.invoke(params)
          }
        } else {
          null
        }
      }
    }

    fromNetwork?.fold(
      onSuccess = { content ->
        suspendRunCatching { intoStorage.invoke(params, content) }.onFailure { error ->
          Logger.e(TAG, error) { "Failed to update storage" }
        }

        suspendRunCatching { intoMemory.invoke(params, content) }.onFailure { error ->
          Logger.e(TAG, error) { "Failed to update memory cache" }
        }

        emit(Async.Content(content))
      },
      onFailure = { error ->
        Logger.e(TAG, error) { "Failed to fetch from network" }

        if (memCached == null && storageCached == null) {
          emit(Async.Error(error))
        }
      },
    )
  }.catch { error ->
    emit(Async.Error(error))
  }

  public sealed interface LoadStrategy {
    public data object CacheFirst : LoadStrategy
    public data object CacheOnly : LoadStrategy
  }

  internal companion object {
    const val TAG: String = "AsyncDataStore"
  }
}