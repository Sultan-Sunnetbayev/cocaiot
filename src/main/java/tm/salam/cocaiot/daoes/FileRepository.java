package tm.salam.cocaiot.daoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tm.salam.cocaiot.models.File;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO files(name, path, extension, size) VALUES(:name, :path, :extension, :size) " +
            "ON CONFLICT DO NOTHING RETURNING CAST(uuid AS VARCHAR)")
    UUID uploadFile(@Param("name")String name,
                    @Param("path")String path,
                    @Param("extension")String extension,
                    @Param("size")long size);

    @Query("SELECT file FROM File file WHERE file.uuid = :fileUuid")
    File getFileByUuid(@Param("fileUuid")UUID fileUuid);

    @Query("SELECT file FROM File file WHERE file.isConfirmed = :isConfirmed")
    List<File> getFilesByConfirmedValue(@Param("isConfirmed")boolean isConfirmed);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM files WHERE is_confirmed = false RETURNIG COUNT(*)")
    int deleteNotConfirmedFile();

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM files WHERE uuid = :fileUuid RETURNING TRUE")
    Boolean deleteFileByUuid(@Param("fileUuid")UUID fileUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE files SET is_confirmed = :statusConfirm WHERE uuid IN :fileUuids")
    void changeStatusConfirmFileByUuid(@Param("statusConfirm")boolean statusConfirm,
                                       @Param("fileUuids")List<UUID>fileUuids);

    @Query("SELECT COUNT(file) FROM File file WHERE file.uuid IN :fileUuids")
    int getAmountFilesByUuids(@RequestParam("fileUuids") Set<UUID> fileUuids);

}
