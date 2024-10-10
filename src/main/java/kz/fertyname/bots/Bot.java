package kz.fertyname.bots;

import kz.fertyname.manager.Schedule;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "";
    private static final String BOT_TOKEN = "";
    private final Schedule schedule;

    public Bot() {
        this.schedule = new Schedule();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, "������! ��� ��� ��� ���23/2\n" +
                            "��� �� ������ ���������� -> /����������\n" +
                            "��� �� ������ ���������� ����� �� �������� -> /����������");
                    break;
                case "/����������":
                    String scheduleMessage = schedule.getScheduleForToday();
                    sendMessage(chatId, scheduleMessage);
                    break;
                case "/����������":
                    String breakMessage = schedule.getBreakMessage();
                    sendMessage(chatId, breakMessage);
                    break;
                default:
                    sendMessage(chatId, "����������� �������.");
                    break;
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
