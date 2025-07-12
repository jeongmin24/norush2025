package com.capstone.norush2025.domain;

import com.capstone.norush2025.code.Authority;
import com.capstone.norush2025.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admins")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Admin extends BaseEntity {
    @Id
    private String adminId;
    private String email;
    private String name;
    private String password;

    private Authority authority = Authority.ADMIN; // 관리자 or 사용자

}
