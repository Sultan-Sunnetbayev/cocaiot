package tm.salam.cocaiot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.services.FileService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload-file", produces = "application/json")
    public ResponseEntity uploadFile(@RequestParam("file")MultipartFile file){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=fileService.uploadFile(file);

        response.put("status",responseTransfer.isStatus());
        response.put("code",responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data",responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-file", params = {"fileUuid"}, produces = "application/json")
    public ResponseEntity getFileByUuid(@RequestParam("fileUuid")UUID fileUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=fileService.getFileDTOByUuid(fileUuid);

        response.put("status",responseTransfer.isStatus());
        response.put("code",responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data",responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
