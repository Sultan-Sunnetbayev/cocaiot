package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.dtoes.CompanyDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.helpers.TypeOwnership;
import tm.salam.cocaiot.models.Company;

import java.util.List;
import java.util.UUID;

public interface CompanyService {

    @Transactional
    ResponseTransfer<UUID> addCompany(Company company, UUID organizationUuid, List<UUID> typeActivityUuids,
                                TypeOwnership typeOwnership, UUID countryUuid, UUID regionUuid,
                                UUID directorUuid, List<UUID> founderUuids, UUID logoUuid, UUID... fileUuids);

    @Transactional
    ResponseTransfer editCompany(Company company, UUID organizationUuid, List<UUID> typeActivityUuids,
                                 TypeOwnership typeOwnership, UUID countryUuid, UUID regionUuid,
                                 UUID directorUuid, List<UUID> founderUuids, UUID logoUuid, UUID... fileUuids);

    @Transactional
    ResponseTransfer removeCompanyByUuid(UUID companyUuid);

    ResponseTransfer getAllCompanyDTOS(String searchKey, int page, int size, List<String> sortBy,
                                       List<SortType> sortTypes, TypeOwnership typeOwnership);

    int getAmountCompaniesBySearchKey(String searchKey, TypeOwnership typeOwnership);

    boolean isCompanyColumnsExists(String withStart, List<String> columns);

    List<String> parseCompanyColumns(String withStart, List<String> columns);

    ResponseTransfer getCompanyDTOByUuid(UUID companyUuid);

    List<CompanyDTO>getIsNotCocaiotMemberCompanyDTOSByName(String searchKey);

    ResponseTransfer getCompanyDTOOnlyGeneralByUuid(UUID companyUuid);

    ResponseTransfer isCompanyExistsByUuid(UUID companyUuid);

    ResponseTransfer getCocaiotMemberCompanyDTOSByName(String searchKey);

    List<CompanyDTO> getCompanyDTOSByName(String searchKey);

    List<CompanyDTO> getCompanyDTOSByTypeActivityUuids(List<UUID> typeActivityUuids);

    List<String>getCompanyEmailsByUuids(List<UUID> companyUuids);

    ResponseTransfer isCompaniesExistsByUuids(List<UUID> companyUuids);

    ResponseTransfer<?> getCompanyNamesByTypeActivityUuid(UUID typeActivityUuid);
}
