package tgbotAi.as.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgbotAi.as.config.AIBotConfig;

import java.util.logging.Logger;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final AIBotConfig aiBotConfig;

    public TelegramBot(@Autowired AIBotConfig aiBotConfig) {
        this.aiBotConfig = aiBotConfig;
    }

    @Override
    public String getBotUsername() {
        return aiBotConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return aiBotConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();

            switch (messageText) {
                case "/start":
                    startRecive(chatId, userName);
                    return;
                default:
                    sendMessage(chatId, "Прости, пока я не умею взаимодействовать с данной командой");
            }
        }
    }

    private void startRecive(Long chatId, String userName) {
        String answer = "Привет " + userName + " рад тебя видеть в этом чате!";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.info("Problem in sendMessage API Telegram");
        }
    }
}
