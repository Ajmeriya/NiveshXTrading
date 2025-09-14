package com.nivesh.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String email,String otp) throws MessagingException {
        MimeMessage mimeMailMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMailMessage,"utf-8");

        String subject="Verify otp";

        String text="your verification code is "+otp;

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text);
        mimeMessageHelper.setTo(email);

        try{
            javaMailSender.send(mimeMailMessage);
        }
        catch(MailException e)
        {
            throw new MailSendException(e.getMessage());
        }

    }

}
