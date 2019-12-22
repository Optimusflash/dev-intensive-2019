package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class ArchiveViewModel : ViewModel() {
    private val chatRepository = ChatRepository

    private val items = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { it.isArchived }
            .map { it.toChatItem() }
    }

    fun getArchiveItems(): LiveData<List<ChatItem>> {
        return items
    }

    fun addToArchive(itemId: String) {
        val chat = chatRepository.find(itemId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(itemId: String) {
        val chat = chatRepository.find(itemId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

}