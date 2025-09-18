package com.project.pillpal.reminder.service;

import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.entity.Reminder;
import com.project.pillpal.reminder.mapper.ReminderMapperImpl;
import com.project.pillpal.reminder.repository.ReminderRepository;
import com.project.pillpal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final ReminderMapperImpl reminderMapperImpl;
    private final UserRepository userRepository;

    public List<ReminderResponse> getAllReminders() {
        List<Reminder> reminders = reminderRepository.findAll();
        return reminders.stream()
                .map(reminder -> reminderMapperImpl.toResponse(reminder))
                .toList();
    }


}
