package com.project.pillpal.reminder.controller;

import com.project.pillpal.reminder.dto.ReminderResponse;
import com.project.pillpal.reminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reminders")
public class ReminderController {
    private final ReminderService reminderService;

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getAllReminders() {
        List<ReminderResponse> reminders = reminderService.getAllReminders();
        return ResponseEntity.ok(reminders);
    }


}
