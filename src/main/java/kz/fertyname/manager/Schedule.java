package kz.fertyname.manager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;

public class Schedule {

    private final String[][] scheduleData = {
            {"12:40-14:00 - ��������", "14:05-15:25 - ����������", "15:40-17:00 - �����", "17:05-18:25 - ���"},
            {"12:40-14:00 - ������������ �����", "14:05-15:25 - ���������", "15:40-17:00 - ���������", "17:05-18:25 - �����"},
            {"13:30-14:35 - ���������", "14:45-15:50 - ���", "16:05-17:10 - ��������", "17:15-18:20 - �������"},
            {"13:20-14:10 - �������", "14:20-15:10 - ���"},
            {"11:10-12:30 - ������������ �����", "12:40-14:00 - ������������ �����/����������", "14:05-15:25 - ����������������", "15:40-17:00 - ����������"},
    };

    private LocalTime getCurrentTimeInAlmaty() {
        ZonedDateTime almatyTime = ZonedDateTime.now(ZoneId.of("Asia/Almaty"));
        return almatyTime.toLocalTime();
    }

    public String getScheduleForToday() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        int dayIndex = dayOfWeek.getValue() - 1;

        if (dayIndex < 0 || dayIndex >= scheduleData.length) {
            return "������� ��������. ��� ����������.";
        }

        StringBuilder schedule = new StringBuilder("���������� �� �������:\n");
        for (String lesson : scheduleData[dayIndex]) {
            schedule.append(lesson).append("\n");
        }
        return schedule.toString();
    }

    public String getBreakMessage() {
        LocalTime currentTime = getCurrentTimeInAlmaty();
        String todaySchedule = getScheduleForToday();
        if (todaySchedule.contains("������� ��������")) {
            return todaySchedule;
        }

        LocalTime nextBreakStart = calculateNextBreakStart(currentTime);
        if (nextBreakStart != null) {
            return timeUntilEnd(currentTime, nextBreakStart, "�������");
        }

        for (String lesson : todaySchedule.split("\n")) {
            String[] parts = lesson.split(" - ");
            if (parts.length > 0) {
                String[] times = parts[0].trim().split("-");
                if (times.length == 2) {
                    LocalTime startTime = LocalTime.parse(times[0].trim());
                    LocalTime endTime = LocalTime.parse(times[1].trim());

                    if (isBetween(currentTime, startTime, endTime)) {
                        return timeUntilEnd(currentTime, endTime, "����");
                    }
                }
            }
        }

        return checkBreakTimes(currentTime);
    }

    private LocalTime calculateNextBreakStart(LocalTime currentTime) {
        LocalTime[][] breakTimes = {
                {LocalTime.of(10, 10), LocalTime.of(11, 30)},
                {LocalTime.of(11, 40), LocalTime.of(13, 0)},
                {LocalTime.of(14, 5), LocalTime.of(15, 25)},
                {LocalTime.of(15, 35), LocalTime.of(16, 55)},
                {LocalTime.of(17, 15), LocalTime.of(18, 35)}
        };

        LocalTime[] nextBreakStarts = {
                LocalTime.of(11, 30),
                LocalTime.of(13, 0),
                LocalTime.of(15, 25),
                LocalTime.of(16, 55),
                LocalTime.of(18, 35)
        };

        for (int i = 0; i < breakTimes.length; i++) {
            if (isBetween(currentTime, breakTimes[i][0], breakTimes[i][1])) {
                return nextBreakStarts[i];
            }
        }
        return null;
    }

    private String checkBreakTimes(LocalTime currentTime) {
        LocalTime[][] breakTimes = {
                {LocalTime.of(11, 30), LocalTime.of(11, 40)},
                {LocalTime.of(13, 0), LocalTime.of(14, 5)},
                {LocalTime.of(15, 25), LocalTime.of(15, 35)},
                {LocalTime.of(16, 55), LocalTime.of(17, 15)}
        };

        for (LocalTime[] times : breakTimes) {
            if (isBetween(currentTime, times[0], times[1])) {
                return "����� ��������.";
            }
        }
        return "������� �� ����, ������ " + LocalTime.now().toString().substring(0, 8) + ".";
    }

    private boolean isBetween(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }

    private String timeUntilEnd(LocalTime currentTime, LocalTime endTime, String periodType) {
        long secondsUntilEnd = java.time.Duration.between(currentTime, endTime).getSeconds();
        long minutes = secondsUntilEnd / 60;
        long seconds = secondsUntilEnd % 60;
        return "�������� ������� �� ����� " + periodType + ": " + minutes + " ����� " + seconds + " ������.";
    }
}
