package com.wwd.tgdb.scheduled;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {
    @Value("${rrc.login}")
    private String rrcLogin;
    @Value("${rrc.password}")
    private String rrcPassword;
    @Value("${rrc.code}")
    private String rrcCode;
    @Value("${rrc.id}")
    private String rrcId;
    @Value("${upload.path}")
    private static String uploadPath;

    public void getGpl() {
        String command = "curl 'https://b2b.rrc.ru/personal/xml?code=" +
                rrcCode + "&id=" +
                rrcId + "&login=" +
                rrcLogin + "&pass=" +
                rrcPassword + "'";

        System.out.println(uploadPath);
        System.out.println(rrcId);
        System.out.println(command);

//        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
//
//        processBuilder.directory(new File(uploadPath));
//
//        Process process = null;
//        try {
//            process = processBuilder.start();
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//        int exitCode = process.exitValue();
//
//        System.out.println(exitCode);
//
//        System.out.println(process);
//
//        process.destroy();
//
//        System.out.println(process);

//        System.out.println(command);
    }
}
