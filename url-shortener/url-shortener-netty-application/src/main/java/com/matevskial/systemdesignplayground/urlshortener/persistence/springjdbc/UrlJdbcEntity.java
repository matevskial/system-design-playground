package com.matevskial.systemdesignplayground.urlshortener.persistence.springjdbc;

import com.matevskial.systemdesignplayground.urlshortener.persistence.common.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("URLS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UrlJdbcEntity implements Entity<Long> {

    @Id
    @Setter
    private Long id;

    @Column(value = "original_url")
    private String originalUrl;

    @Column(value = "shortened")
    private String shortened;

    public static UrlJdbcEntity newEntity(String url, String shortened) {
        return new UrlJdbcEntity(null, url, shortened);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UrlJdbcEntity that = (UrlJdbcEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
