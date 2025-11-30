package com.pragma.powerup.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Metadatos de paginación
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMeta {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

