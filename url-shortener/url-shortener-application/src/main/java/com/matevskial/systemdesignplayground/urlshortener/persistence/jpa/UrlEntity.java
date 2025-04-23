package com.matevskial.systemdesignplayground.urlshortener.persistence.jpa;

import com.matevskial.systemdesignplayground.urlshortener.jpautils.entity.EntityListener;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;

@Entity
@Table(name = "URLS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners({EntityListener.class})
public class UrlEntity implements com.matevskial.systemdesignplayground.urlshortener.jpautils.entity.Entity<Long> {

    @PersistenceCreator
    public static UrlEntity of(Long id, String url, String shortened) {
        return new UrlEntity(id, url, shortened);
    }

    @Id
    @Setter
    private Long id;

    @Column(name = "original_url", unique = true)
    private String originalUrl;

    @Column(name = "shortened", unique = true)
    private String shortened;

    public static UrlEntity newEntity(String url, String shortened) {
        return new UrlEntity(null, url, shortened);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlEntity that = (UrlEntity) o;
        return this.id != null && Objects.equals(that.id, this.id);
    }
}
