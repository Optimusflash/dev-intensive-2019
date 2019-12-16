package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    val members: List<User> = mutableListOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {
    fun unreadableMessageCount(): Int{
        var counter = 0
        messages.forEach{
            if (!it.isReaded){
                counter++
            }
        }
        return counter
    }

    fun lastMessageDate(): Date?{
        return messages.maxBy { it.date }?.date
    }

    fun lastMessageShort(): Pair<String, String>{
        return if(messages.isEmpty()){
            "сообщений пока нет" to ""
        } else {
            val tempMessage = messages
            tempMessage.sortBy { it.date }
            val lastMessage = tempMessage.last()
            if (lastMessage is TextMessage){
                "${lastMessage.text}" to "${lastMessage.from?.firstName}"
            } else{
                "${lastMessage.from?.firstName} - отправил фото" to "${lastMessage.from?.firstName}"
            }
        }
    }

    private fun isSingle(): Boolean = members.size==1
    
    fun toChatItem(): ChatItem {
        return if (isSingle()){
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName?:""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else{
            ChatItem(
                id,
                null,
                "",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }
}

enum class ChatType{
    SINGLE,
    GROUP,
    ARCHIVE
}

