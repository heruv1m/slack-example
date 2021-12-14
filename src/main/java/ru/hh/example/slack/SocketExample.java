package ru.hh.example.slack;


import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;

public class SocketExample {
  public static void main(String[] args) throws Exception {
        String botToken = "xoxb-YOUR-BOT-TOKEN";
        String appToken = "xapp-APPTOKEN";

    AppConfig appConfig = AppConfig.builder().singleTeamBotToken(botToken).build();
    App app = new App(appConfig);

    app.event(AppMentionEvent.class, (req, ctx) -> {
      System.out.println("HI! I received event!" + req.getEvent());
      ctx.say("I was mentioned by user with id " + req.getEvent().getUser());
      return ctx.ack();
    });

    SocketModeApp socketModeApp = new SocketModeApp(appToken, app);

// #start() method establishes a new WebSocket connection and then blocks the current thread.
// If you do not want to block this thread, use #startAsync() instead.
    socketModeApp.start();
  }
}
