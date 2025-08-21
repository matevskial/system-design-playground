package com.matevskial.systemdesignplayground.urlshortener.persistence.hibernate;

import com.matevskial.systemdesignplayground.urlshortener.hibernate.entity.EntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "URLS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners({EntityListener.class})
public class UrlEntity implements com.matevskial.systemdesignplayground.urlshortener.hibernate.entity.Entity<Long> {

    @Id
    @Setter
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

    public static UrlEntity newEntity(String url, String shortened) {
        return new UrlEntity(null, url, shortened);
    }
}
