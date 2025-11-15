package com.capstone.norush2025.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleTasksService {

    private static final String APPLICATION_NAME = "NoRush Application";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final OAuth2AuthorizedClientService clientService;
    
    private Tasks getTasksClient(OAuth2AuthenticationToken authentication) throws IOException, GeneralSecurityException {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (client == null) {
            throw new IllegalStateException("Google OAuth2 Authorized Client를 찾을 수 없습니다.");
        }

        String accessToken = client.getAccessToken().getTokenValue();
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return new Tasks.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // 기본 할 일 목록 ID("@default")를 반환
    public String getPrimaryTaskListId(OAuth2AuthenticationToken authentication) throws Exception {
        return "@default"; 
    }

    // Google Tasks에 새 할 일(메모)을 생성
    public Task createNewTask(
            OAuth2AuthenticationToken authentication,
            String title,
            String notes) throws Exception {

        Tasks service = getTasksClient(authentication);
        String tasklistId = getPrimaryTaskListId(authentication);

        Task task = new Task()
                .setTitle(title)
                .setNotes(notes);
        
        log.info("Google Tasks에 할 일 생성 시도: {}", title);
        task = service.tasks().insert(tasklistId, task).execute();
        log.info("Google Task 생성 성공. Task ID: {}", task.getId());

        return task;
    }

    // Google Tasks 목록을 조회합니다. (참고용)
    public List<Task> getTasks(OAuth2AuthenticationToken authentication) throws Exception {
        Tasks service = getTasksClient(authentication);
        String tasklistId = getPrimaryTaskListId(authentication);
        return service.tasks().list(tasklistId).execute().getItems();
    }
}