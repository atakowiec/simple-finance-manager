package pl.pollub.backend.group.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupExportResponse {
    private final byte[] data;
    private final String contentType;
    private final String filename;
}
