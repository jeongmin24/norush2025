package com.capstone.norush2025.service;

import com.capstone.norush2025.code.ErrorCode;
import com.capstone.norush2025.domain.Memo;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.request.MemoAddRequest;
import com.capstone.norush2025.dto.request.MemoUpdateRequest;
import com.capstone.norush2025.dto.response.MemoResponse;
import com.capstone.norush2025.exception.BusinessLogicException;
import com.capstone.norush2025.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserService userService;

    @Transactional
    public MemoResponse.MemoInfo addMemo(String userId, MemoAddRequest request) {
        User user = userService.getUser(userId);

        Memo newMemo = Memo.builder()
                .userId(user.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        Memo savedMemo = memoRepository.save(newMemo);
        return new MemoResponse.MemoInfo(savedMemo);
    }

    @Transactional(readOnly = true)
    public List<MemoResponse.MemoInfo> getMemos(String userId) {
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
