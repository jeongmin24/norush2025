package com.capstone.norush2025.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
/**
 * MongoDB Auditing(생성일/수정일 자동화)을 켜주는 설정 클래스
 * */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
