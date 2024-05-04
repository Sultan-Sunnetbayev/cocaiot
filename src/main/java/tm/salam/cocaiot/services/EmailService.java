package tm.salam.cocaiot.services;

import tm.salam.cocaiot.dtoes.FileDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.models.Mailing;

import java.util.List;

public interface EmailService {

    void sendSimpleMessage(Mailing mailing, List<String> emails);

    ResponseTransfer sendMessageWithAttachment(Mailing mailing, FileDTO fileDTO,
                                               List<String> emails);
}
