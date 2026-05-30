package com.example.tounip.tounip.translation.infrastructure.persistence.entity;

import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "message_translation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_message_translation_message_target",
                        columnNames = {"message_id", "target_language"}
                )
        }
)
public class MessageTranslationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MessageEntity message;

    @Column(name = "source_language", nullable = false, length = 20)
    private String sourceLanguage;

    @Column(name = "target_language", nullable = false, length = 20)
    private String targetLanguage;

    @Column(name = "original_text_snapshot", nullable = false, columnDefinition = "text")
    private String originalTextSnapshot;

    @Column(name = "translated_text", nullable = false, columnDefinition = "text")
    private String translatedText;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}