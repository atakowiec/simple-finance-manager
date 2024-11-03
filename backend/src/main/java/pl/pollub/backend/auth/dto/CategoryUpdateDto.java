package pl.pollub.backend.auth.dto;

import lombok.Data;

@Data
public class CategoryUpdateDto {
    private Long id;
    private String name;
    private String icon;
}
