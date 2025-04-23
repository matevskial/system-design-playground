package com.matevskial.systemdesignplayground.urlshortener.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;

@Entity
@Table(name = "URLS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UrlEntity {

    @PersistenceCreator
    public static UrlEntity of(Long id, String url, String shortened) {
        return new UrlEntity(id, url, shortened);
    }

    @Id
    private Long id;

    @Column(name = "original_url", unique = true)
    private String originalUrl;

    @Column(name = "shortened", unique = true)
    private String shortened;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlEntity that = (UrlEntity) o;
        return this.id != null && Objects.equals(that.id, this.id);
    }
}
