package tm.salam.cocaiot.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static ResponseTransfer uploadFile(final String uploadDir, final String fileName, final MultipartFile file){

        ResponseTransfer responseTransfer;
        final Path path=Paths.get(uploadDir);

        if(!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                responseTransfer=ResponseTransfer.builder()
                        .status(false)
                        .code("SR-00012")
                        .message(ioException.getMessage())
                        .httpStatus(HttpStatus.GATEWAY_TIMEOUT)
                        .build();

                return responseTransfer;
            }
        }
        try {
            InputStream inputStream = file.getInputStream();
            Path fullPath=path.resolve(fileName);

            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00008")
                    .message("accept file successful uploaded")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
            inputStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00012")
                    .message(ioException.getMessage())
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }

        return responseTransfer;
    }

}
