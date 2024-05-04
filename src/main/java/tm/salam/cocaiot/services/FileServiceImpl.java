package tm.salam.cocaiot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tm.salam.cocaiot.daoes.FileRepository;
import tm.salam.cocaiot.dtoes.FileDTO;
import tm.salam.cocaiot.helpers.FileUploadUtil;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.models.File;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class FileServiceImpl implements FileService{

    @Value("${file.upload.path}")
    private String fileUploadPath;

    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    @Transactional
    public ResponseTransfer uploadFile(final MultipartFile file){

        final String fileName= UUID.randomUUID().toString()+"_"+file.getOriginalFilename();
        ResponseTransfer responseTransfer= FileUploadUtil.uploadFile(fileUploadPath, fileName, file);

        if(responseTransfer.isStatus()){
            final UUID savedFileUuid=fileRepository.uploadFile(file.getOriginalFilename(), fileUploadPath+"/"+fileName,
                    file.getContentType(), file.getSize());

            if(savedFileUuid!=null){
                responseTransfer.setData(FileDTO.builder()
                        .uuid(savedFileUuid)
                        .name(file.getOriginalFilename())
                        .path(fileUploadPath+"/"+fileName)
                        .extension(file.getContentType())
                        .size(file.getSize())
                        .build());
            }else{
                responseTransfer= ResponseTransfer.builder()
                        .status(false)
                        .code("SR-00013")
                        .message("error with saving file in database")
                        .httpStatus(HttpStatus.FAILED_DEPENDENCY)
                        .build();
            }
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public void changeStatusConfirmFilesByUuid(final boolean statusConfirm, final UUID... fileUuids){

        List<UUID>uploadedFileUuids=new LinkedList<>();

        for(UUID fileUuid:fileUuids){
            if(fileUuid!=null){
                uploadedFileUuids.add(fileUuid);
            }
        }
        fileRepository.changeStatusConfirmFileByUuid(statusConfirm, uploadedFileUuids);

        return;
    }

    @Transactional
    @Async
    @Scheduled(cron = "0 01 02 * * *")
    void removeNotConfirmedFiles(){

        List<File> files=fileRepository.getFilesByConfirmedValue(false);

        if(files==null){

            return;
        }
        Date currentDate=new Date();

        for(File file:files){

            long diff= TimeUnit.MILLISECONDS.toHours(currentDate.getTime()-file.getCreated().getTime());

            if(diff>=24){
                java.io.File removedFile=new java.io.File(file.getPath());

                if(removedFile.exists()){
                    removedFile.delete();
                }
                fileRepository.deleteFileByUuid(file.getUuid());
            }
        }

        return;
    }

    @Override
    public ResponseTransfer getFileDTOByUuid(final UUID fileUuid){

        ResponseTransfer responseTransfer;
        File file=fileRepository.getFileByUuid(fileUuid);

        if(file==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00015")
                    .message("error file not found with this file uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00010")
                    .message("accept file successful returned")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toDTO(file))
                    .build();
        }

        return responseTransfer;
    }

    private FileDTO toDTO(final File file){

        if(file==null){

            return null;
        }
        FileDTO fileDTO=FileDTO.builder()
                .uuid(file.getUuid())
                .name(file.getName())
                .path(file.getPath())
                .extension(file.getExtension())
                .size(file.getSize())
                .build();

        return fileDTO;
    }

    @Override
    public ResponseTransfer isFilesExistsByUuids(UUID... fileUuids){

        final ResponseTransfer responseTransfer;
        Set<UUID> uploadFileUuids=new HashSet<>();

        for(UUID fileUuid:fileUuids){
            if(fileUuid!=null){
                uploadFileUuids.add(fileUuid);
            }
        }
        int amountFiles=fileRepository.getAmountFilesByUuids(uploadFileUuids);
        if(amountFiles!=uploadFileUuids.size()){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00054")
                    .message("error with upload files")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .build();
        }

        return responseTransfer;
    }

}
