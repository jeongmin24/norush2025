package com.capstone.norush2025.service;

import com.capstone.norush2025.code.ErrorCode;
import com.capstone.norush2025.domain.Memo;
import com.capstone.norush2025.domain.user.AuthProvider;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.request.MemoAddRequest;
import com.capstone.norush2025.dto.request.MemoUpdateRequest;
import com.capstone.norush2025.dto.response.MemoResponse;
import com.capstone.norush2025.exception.BusinessLogicException;
import com.capstone.norush2025.repository.MemoRepository;
import com.google.api.services.tasks.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // 💡 [추가]
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserService userService;

    private final Optional<GoogleTasksService> googleTasksService; 

    /**
     * 메모를 추가하고, Google 로그인 사용자일 경우 Google Tasks에 동기화합니다.
     * @param userId 현재 로그인 사용자 ID
     * @param request 메모 추가 요청 DTO
     * @param authentication OAuth2 인증 토큰 (Google 로그인 시에만 값 존재)
     * @return 저장된 메모 정보
     */
    @Transactional
    public MemoResponse.MemoInfo addMemo(
            String userId, 
            MemoAddRequest request,
            Optional<OAuth2AuthenticationToken> authentication) { // 💡 [변경] Optional<Authentication> 파라미터 추가
        
        User user = userService.getUser(userId);

        Memo newMemo = Memo.builder()
                .userId(user.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        Memo savedMemo = memoRepository.save(newMemo);

        if (user.getProvider() == AuthProvider.GOOGLE && authentication.isPresent() && googleTasksService.isPresent()) {
            try {
                Task googleTask = googleTasksService.get().createNewTask(
                    authentication.get(), 
                    request.getTitle(), 
                    request.getContent()
                );
                
               
                log.info("Google Tasks에 메모 동기화 성공 (사용자: {}), Task ID: {}", userId, googleTask.getId());

            } catch (Exception e) {

                log.error("Google Tasks 동기화 실패 (사용자: {}): {}", userId, e.getMessage());
            }
        }
        
        return new MemoResponse.MemoInfo(savedMemo);
    }
    

    @Transactional(readOnly = true)
    public List<MemoResponse.MemoInfo> getMemos (String userId) {
        User user = userService.getUser(userId);

        return memoRepository.findByUserId(user.getUserId()).stream()
                .map(MemoResponse.MemoInfo::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemoResponse.MemoInfo getMemoByUserId(String memoId, String userId) {
        User user = userService.getUser(userId);

        Memo memo = findMemoByMemoIdAndUserId(memoId, user.getUserId());

        return new MemoResponse.MemoInfo(memo);
    }

    @Transactional
    public MemoResponse.MemoInfo updateMemo(String memoId, String userId, MemoUpdateRequest request) {
        User user = userService.getUser(userId);

        Memo existingMemo = findMemoByMemoIdAndUserId(memoId, user.getUserId());

        existingMemo.updateMemo(
                request.getTitle(),
                request.getContent()
        );

        Memo updatedMemo = memoRepository.save(existingMemo);
        return new MemoResponse.MemoInfo(updatedMemo);
    }

    @Transactional
    public void deleteMemo(String memoId, String userId) {
        User user = userService.getUser(userId);

        Memo memoToDelete = findMemoByMemoIdAndUserId(memoId, user.getUserId());

        memoRepository.delete(memoToDelete);
    }


    private Memo findMemoByMemoIdAndUserId(String memoId, String userId) {
        return memoRepository.findByMemoIdAndUserId(memoId, userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.MEMO_NOT_FOUND.getMessage()));
    }
}