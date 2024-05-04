package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tm.salam.cocaiot.helpers.ResponseTransfer;

import java.util.UUID;

public interface FileService {

    @Transactional
    ResponseTransfer uploadFile(MultipartFile file);

    @Transactional
    void changeStatusConfirmFilesByUuid(boolean statusConfirm, UUID... fileUuids);

    ResponseTransfer getFileDTOByUuid(UUID fileUuid);

    ResponseTransfer isFilesExistsByUuids(UUID... fileUuids);
}
