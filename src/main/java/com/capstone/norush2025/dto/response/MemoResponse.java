package com.capstone.norush2025.dto.response;

import com.capstone.norush2025.domain.Memo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class MemoResponse {

    @Getter
    @Setter
    public static class MemoInfo {
        private String memoId;
        private String userId;
        private String title;
        private String content;
        private LocalDateTime timestamp;

        public MemoInfo(Memo memo) {
            this.memoId = memo.getMemoId();
            this.userId = memo.getUserId();
            this.title = memo.getTitle();
            this.content = memo.getContent();
            this.timestamp = memo.getTimestamp();
        }
    }
}
