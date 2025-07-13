package com.capstone.norush2025.domain;

import com.capstone.norush2025.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "memos")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Memo extends BaseEntity {
    @Id
    private String memoId;

    private String userId;
    private String title;
    private String content;

    private LocalDateTime timestamp;

    public void updateMemo(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
