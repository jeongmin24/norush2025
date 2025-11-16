package com.capstone.norush2025.domain;

import com.capstone.norush2025.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "saved_route")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteDocument extends BaseEntity {

    @Id
    private Long routeId;
    private String userId;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;

}
