package tgbotAi.as.services;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgbotAi.as.config.AIBotConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                    sendMessage(chatId, chatGPT_Answer(messageText));
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

    private String chatGPT_Answer(String messageToGPT) {

        //Запрос к ЧатГпт

        OpenAiService service = new OpenAiService(aiBotConfig.getGptToken());
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(messageToGPT)
                .model("ada")
                .echo(true)
                .build();
        return service.createCompletion(completionRequest).getChoices().toString();

        //Работа с картинками


//        System.out.println("\nCreating completion...");
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .model("ada")
//                .prompt(messageToGPT)
//                .echo(true)
//                .user("testing")
//                .n(3)
//                .build();
//        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
//
//        System.out.println("\nCreating Image...");
//        CreateImageRequest request = CreateImageRequest.builder()
//                .prompt(messageToGPT)
//                .build();
//
//        System.out.println("\nImage is located at:");
//        System.out.println(service.createImage(request).getData().get(0).getUrl());

//        System.out.println("Streaming chat completion...");
//        final List<ChatMessage> messages = new ArrayList<>();
//        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), messageToGPT);
//        messages.add(systemMessage);
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
//                .builder()
//                .model("gpt-3.5-turbo")
//                .messages(messages)
//                .n(1)
//                .maxTokens(50)
//                .logitBias(new HashMap<>())
//                .build();
//
//        service.streamChatCompletion(chatCompletionRequest)
//                .doOnError(Throwable::printStackTrace)
//                .blockingForEach(System.out::println);
//
//        service.shutdownExecutor();
//
//        return chatCompletionRequest.getMessages().toString();
    }

}
