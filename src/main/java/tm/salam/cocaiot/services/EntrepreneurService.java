package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.dtoes.EntrepreneurDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;

import java.util.List;
import java.util.UUID;

public interface EntrepreneurService {

    @Transactional
    ResponseTransfer<UUID> addEntrepreneur(UUID personUuid,
                                     String webSite,
                                     String typeWork,
                                     List<UUID>typeActivityUuids,
                                     UUID logoUuid,
                                     UUID membershipApplicationUuid,
                                     UUID patentCertifyingPaymentUuid,
                                     UUID entrepreneurStatisticalCodes,
                                     UUID certificateOfForeignEconomicRelationsUuid,
                                     UUID registrationCertificateOfEntrepreneur,
                                     UUID certificateOfTaxRegistration);

    @Transactional
    ResponseTransfer<?> editEntrepreneur(UUID entrepreneurUuid, UUID personUuid, String webSite, String typeWork,
                                      List<UUID>typeActivityUuids, UUID logoUuid, UUID... fileUuids);

    int getAmountEntrepreneursBySearchKey(String searchKey);

    ResponseTransfer<List<EntrepreneurDTO>> getAllEntrepreneurDTOS(String searchKey, int page, int size, List<String> sortBy,
                                            List<SortType> sortTypes);

    ResponseTransfer<EntrepreneurDTO> getEntrepreneurDTOByUuid(UUID entrepreneurUuid);

    @Transactional
    ResponseTransfer<?> removeEntrepreneurByUuid(UUID entrepreneurUuid);

    List<EntrepreneurDTO>getIsNotCocaiotMemberEntrepreneurDTOSByFullName(String searchKey);

    ResponseTransfer getEntrepreneurDTOOnlyGeneralByUuid(UUID entrepreneurUuid);

    ResponseTransfer isEntrepreneurExistsByUuid(UUID entrepreneurUuid);

    List<EntrepreneurDTO>getEntrepreneurDTOSByFullName(String searchKey);

    List<EntrepreneurDTO>getEntrepreneurDTOSByTypeActivityUuids(List<UUID> typeActivityUuids);

    List<String>getEntrepreneurEmailsByUuids(List<UUID> entrepreneurUuids);

    ResponseTransfer isEntrepreneursExistsByUuids(List<UUID> entrepreneurUuids);

    ResponseTransfer<?>getEntrepreneurNamesByTypeActivityUuid(UUID typeActivityUuid);
}
