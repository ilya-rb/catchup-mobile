package com.illiarb.catchup.message

import com.illiarb.catchup.core.arch.message.MessageDispatcher
import com.illiarb.catchup.core.arch.message.MessageDispatcher.Message
import com.illiarb.catchup.core.arch.message.MessageProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class ToastMessageDispatcher : MessageDispatcher, MessageProvider {

  private val _messages = MutableStateFlow<Message?>(null)

  override val messages: Flow<Message?> = _messages

  override fun sendMessage(message: Message) {
    _messages.tryEmit(message)
  }

  fun dismissMessage() {
    _messages.value = null
  }
}