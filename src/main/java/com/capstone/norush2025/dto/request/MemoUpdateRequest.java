package com.capstone.norush2025.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemoUpdateRequest {

    @Size(max = 50, message = "제목은 최대 50자까지 가능합니다.")
    private String title;

    @Size(max = 100, message = "내용은 최대 100자까지 가능합니다.")
    private String content;
}
