package com.capstone.norush2025.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "NoRush Application";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final OAuth2AuthorizedClientService clientService;

    private Calendar getCalendarClient(OAuth2AuthenticationToken authentication) throws IOException, GeneralSecurityException {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (client == null) {
            throw new IllegalStateException("Google OAuth2 Authorized Client를 찾을 수 없습니다.");
        }

        String accessToken = client.getAccessToken().getTokenValue();
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Event createCalendarEvent(
            OAuth2AuthenticationToken authentication,
            String summary,
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime)
            throws Exception {
        
        Calendar service = getCalendarClient(authentication);
        String calendarId = "primary";

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        DateTime startGoogleDateTime = new DateTime(Date.from(startDateTime.atZone(seoulZone).toInstant()));
        DateTime endGoogleDateTime = new DateTime(Date.from(endDateTime.atZone(seoulZone).toInstant()));
        
        EventDateTime start = new EventDateTime()
                .setDateTime(startGoogleDateTime)
                .setTimeZone(seoulZone.getId());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endGoogleDateTime)
                .setTimeZone(seoulZone.getId());
        event.setEnd(end);

        log.info("Google Calendar에 일정 생성 시도: {}", summary);
        event = service.events().insert(calendarId, event).execute();
        log.info("Google Calendar 일정 생성 성공. Event ID: {}", event.getId());
        
        return event;
    }
}