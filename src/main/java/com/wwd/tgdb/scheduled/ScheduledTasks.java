package com.wwd.tgdb.scheduled;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Data
public class ScheduledTasks {
    private String rrcLogin;
    private String rrcPassword;
    private String rrcCode;
    private String rrcId;
    private String uploadPath;

    public void getGpl() {
        String command = "curl 'https://b2b.rrc.ru/personal/xml?code=" +
                rrcCode + "&id=" +
                rrcId + "&login=" +
                rrcLogin + "&pass=" +
                rrcPassword + "'";

//        System.out.println(command);

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        processBuilder.directory(new File("./1.txt"));

        Process process = null;
        try {
            process = processBuilder.start();
            process.waitFor();
//            process.destroy();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
//        int exitCode = process.exitValue();

        System.out.println(process.exitValue());

        System.out.println(process);

        process.destroy();

        System.out.println(process);

//        System.out.println(command);
    }
}
