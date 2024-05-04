package tm.salam.cocaiot.services;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tm.salam.cocaiot.dtoes.FileDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.models.Mailing;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendSimpleMessage(final Mailing mailing, final List<String> emails){

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();

        simpleMailMessage.setFrom("ADMINSTRATOR-TSSE");
        simpleMailMessage.setSubject(mailing.getName());
        simpleMailMessage.setText(mailing.getText());
        simpleMailMessage.setTo(emails.stream().toArray(String[]::new));
        javaMailSender.send(simpleMailMessage);

        return;
    }

    @Override
    public ResponseTransfer sendMessageWithAttachment(final Mailing mailing, final FileDTO fileDTO,
                                                      final List<String> emails){

        final ResponseTransfer responseTransfer;
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper=new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("ADMINSTRATOR-TSSE");
            mimeMessageHelper.setTo(emails.stream().toArray(String[]::new));
            mimeMessageHelper.setSubject(mailing.getName());
            mimeMessageHelper.setText(mailing.getText());

            if(fileDTO!=null) {
                final File file = new File(fileDTO.getPath());
                if (file.exists()) {
                    FileSystemResource fileSystemResource = new FileSystemResource(file.getAbsolutePath());
                    mimeMessageHelper.addAttachment(fileDTO.getName(), fileSystemResource);
                }
            }
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message(messagingException.getMessage())
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();

            return responseTransfer;
        }
        javaMailSender.send(mimeMessage);
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .build();

        return responseTransfer;
    }

}
