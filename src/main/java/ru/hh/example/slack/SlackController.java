package ru.hh.example.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.model.Conversation;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlackController.class);

  @PostMapping(path = "/", consumes = {APPLICATION_FORM_URLENCODED_VALUE})
  public String slackCommand(@RequestParam Map<String, String> slackRequest) {
    LOGGER.info("slackRequest: {}", slackRequest);
    return "hello " + slackRequest.get("user_name");
  }

  @PostMapping(path = "/send-to-chat")
  public void sendMessage() throws SlackApiException, IOException {
    // https://api.slack.com/authentication/oauth-v2
    String token = "xoxb-YOUR-TOKEN";
    String channelName = "YOUR-CHANNEL-NAME";
    String message = "hello";

    var client = Slack.getInstance().methods(token);
    var channelId = findChannel(client, channelName);

    ChatPostMessageResponse chatPostMessageResponse = postMessage(client, channelId, message);
    LOGGER.info("chatPostMessageResponse.isOk() : {}", chatPostMessageResponse.isOk());
  }

  // https://api.slack.com/messaging/retrieving#finding_conversation
  private String findChannel(MethodsClient client, String channelName) throws SlackApiException, IOException {
    String nextCursor = null;
    do {
      var result = getConversationsList(client, nextCursor);
      nextCursor = result.getResponseMetadata().getNextCursor();
      for (Conversation channel : result.getChannels()) {
        if (channel.getName().equalsIgnoreCase(channelName)) {
          return channel.getId();
        }
      }
    } while (nextCursor != null);
    throw new IllegalStateException();
  }

  private ConversationsListResponse getConversationsList(MethodsClient client, String nextCursor) throws SlackApiException, IOException {
    return client.conversationsList(r -> r.cursor(nextCursor));
  }

  // https://api.slack.com/messaging/sending#publishing
  private ChatPostMessageResponse postMessage(MethodsClient client, String conversationId, String message) throws SlackApiException, IOException {
    return client.chatPostMessage(r -> r.channel(conversationId).text(message));
  }
}
