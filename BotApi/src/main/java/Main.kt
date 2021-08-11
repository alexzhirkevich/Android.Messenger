import com.community.botapi.ActionHandler
import com.community.botapi.CommunityBot
import com.community.botapi.MessageHandler
import com.community.botapi.database.imp.Message
import com.community.botapi.database.imp.User

const val KEY = "qwerty"

fun main() {
    KtBot(KEY).run()
}

class KtBot(override val key: String) : CommunityBot() {

    @MessageHandler(types = [MessageHandler.Type.ALL])
    fun echo(message: Message) {
        val response = Message.Builder(message.chatId)
            .setText(message.text)
            .build()
        sendMessage(response)
    }

    @ActionHandler(types = [ActionHandler.Type.START])
    fun onContentMessageReceiver(user : User, action : ActionHandler.Type) {
        addUserToDB(user)
    }

    fun addUserToDB(user : User){
        // todo ....
    }
}
