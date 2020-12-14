package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Word;
import liquibase.pro.packaged.I;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.List;

public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String subject, String from, String to, InputStreamSource source) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setReplyTo(from);
            helper.setText("Parsed data", false);
            helper.addAttachment("wordstat.xlsx", source);
            mailSender.send(message);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void excelCreation(List<Word> result, String to) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row row = sheet.createRow(0);

        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("WORD");
        cell = row.createCell(2);
        cell.setCellValue("COUNT");
        int i = 1;

        for (Word word : result){
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(word.getId());
            cell = row.createCell(1);
            cell.setCellValue(word.getWord());
            cell = row.createCell(2);
            cell.setCellValue(word.getCount());
            i++;
        }

//        File fullFile = new File("wordstat.xlsx");
//        ImageIO.write(file, format, fullFile);

        ByteArrayOutputStream out = null;
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            out = new ByteArrayOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        outputStream.write(out);
        assert out != null;
        InputStreamSource attachment = new ByteArrayResource(out.toByteArray());
        sendMail("Statistics", "tg.adwards.bot@gmail.com", to, attachment);
    }
}
