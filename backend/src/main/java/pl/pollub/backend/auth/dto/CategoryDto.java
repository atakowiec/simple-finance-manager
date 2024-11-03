package pl.pollub.backend.auth.dto;

import lombok.Data;


@Data
public class CategoryDto {
    private long id;
    private String name;
    private String icon;
}
