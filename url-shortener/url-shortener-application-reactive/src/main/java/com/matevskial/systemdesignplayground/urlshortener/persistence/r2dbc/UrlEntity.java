package com.matevskial.systemdesignplayground.urlshortener.persistence.r2dbc;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("URLS")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UrlEntity implements com.matevskial.systemdesignplayground.urlshortener.jpautils.entity.Entity<Long> {

    @Id
    @Setter
    private Long id;

    @Column("original_url")
    private String originalUrl;

    @Column("shortened")
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
