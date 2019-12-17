package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository
import ru.skillbranch.devintensive.utils.DataGenerator

class MainViewModel: ViewModel() {
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()){chats ->

        val resultItems: MutableList<ChatItem> = mutableListOf()

        val notArchivedItems = chats.filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }

        val archivedItems = chats.filter { it.isArchived }


        if (archivedItems.isEmpty()){
             resultItems.addAll(notArchivedItems)
        } else{
           resultItems.add(0,createArchivedItem(archivedItems))
           resultItems.addAll(notArchivedItems)
        }

        return@map resultItems.toList()
    }

    private fun createArchivedItem(archivedItems: List<Chat>): ChatItem {
        val chat = archivedItems.sortedByDescending { it.lastMessageDate() }.first()
        val count = archivedItems.sumBy { it.unreadableMessageCount() }

        return ChatItem(
            id = "-1",
            avatar = null,
            initials = "",
            title = "Архив чатов",
            shortDescription = chat.lastMessageShort().first,
            messageCount = count,
            lastMessageDate = chat.lastMessageDate()?.shortFormat(),
            chatType = ChatType.ARCHIVE
        )

    }

    fun getChatData(): LiveData<List<ChatItem>> {
        return chats
    }


    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String){
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }
}