package com.wwd.tgdb.scheduled;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
                rrcId + /*"&login=" +
                rrcLogin +*/ "&pass=" +
                rrcPassword + "'";

//        System.out.println(command);

        ProcessBuilder processBuilder = new ProcessBuilder(command);

//        processBuilder.directory(new File("/home"));

        Process process = null;
        try {
            process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            System.out.println(Arrays.toString(inputStream.readAllBytes()));
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
