package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.helpers.MemberType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CocaiotMemberService {

    @Transactional
    ResponseTransfer addMember(UUID memberUuid, MemberType memberType, UUID fileUuid,
                               Date initialDateLastPayment, Date finalDateLastPayment);

    @Transactional
    ResponseTransfer editMember(UUID memberUuid, UUID newMemberUuid,
                                MemberType memberType, UUID fileUuid,
                                Date initialDateLastPayment, Date finalDateLastPayment);

    @Transactional
    ResponseTransfer removeMemberByUuid(UUID memberUuid);

    ResponseTransfer getAllCocaiotMemberDTOSBySearchKey(String searchKey, int page, int size,
                                                        List<String> sortBy, List<SortType> sortTypes,
                                                        MemberType memberType);

    int getAmountMembersBySearchKey(String searchKey, MemberType memberType);

    ResponseTransfer getCocaiotMemberDTOByUuid(UUID cocaiotMemberUuid);

}
