package zoz.cool.javis.dto;

import lombok.Data;

@Data
public class FileDTO {
    private byte[] bytes;
    private String contentType;
    private String fileName;

    public FileDTO(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }
}
