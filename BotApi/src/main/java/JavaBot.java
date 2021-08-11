import com.community.botapi.CommunityBot;
import com.community.botapi.MessageHandler;
import com.community.botapi.database.imp.Message;
import org.jetbrains.annotations.NotNull;

class JavaBot extends CommunityBot {

	public static void main(String[] args) {
		new JavaBot().run();
	}

	@MessageHandler(types = MessageHandler.Type.TEXT)
	void onReceive(Message message){

	}

	@NotNull
	@Override
	public String getKey() {
		return "qwerty";
	}
}