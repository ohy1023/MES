-- 사용자 정보 테이블
CREATE TABLE `users`
(
    -- 내부적으로만 사용될 Primary Key (성능 및 관계 설정 용이)
    `id`         BIGINT AUTO_INCREMENT COMMENT '내부 사용자 식별자',

    -- 외부에 노출되는 식별자 (URL, API 등에서 사용)
    `user_uuid`  BINARY(16)   NOT NULL COMMENT '외부용 UUID 식별자',

    `name`       VARCHAR(255) NOT NULL COMMENT '사용자 이름',
    `email`      VARCHAR(255) NOT NULL COMMENT '사용자 이메일',
    `password`   VARCHAR(255) NOT NULL COMMENT '해싱된 비밀번호',

    -- 소프트 델리트를 위한 컬럼
    `deleted_at` TIMESTAMP    NULL DEFAULT NULL COMMENT '삭제 일시',
    `created_at` TIMESTAMP         DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    `updated_at` TIMESTAMP         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 일시',

    -- 기본 키 설정
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_users_user_uuid` (`user_uuid`)

);
