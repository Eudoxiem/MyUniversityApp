package com.myuniversity.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService service;

    @BeforeEach
    void setUp() {
        service = new EmailService(mailSender);
        ReflectionTestUtils.setField(service, "expediteur", "noreply@university.com");
    }

    @Test
    void envoyerEmail_shouldSendWithCorrectFields() {
        service.envoyerEmail("destinataire@test.com", "Sujet", "Corps du message");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals("noreply@university.com", message.getFrom());
        assertEquals("destinataire@test.com", message.getTo()[0]);
        assertEquals("Sujet", message.getSubject());
        assertEquals("Corps du message", message.getText());
    }
}
