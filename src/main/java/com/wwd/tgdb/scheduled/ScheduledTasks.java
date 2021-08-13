package com.wwd.tgdb.scheduled;

import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduledTasks {
    private String rrcLogin;
    private String rrcPassword;
    private String rrcCode;
    private String rrcId;
    private String uploadPath;

    public void getGpl() {
        String generateCommand = "curl \"https://b2b.rrc.ru/personal/xml?code=" +
                rrcCode + "&id=" +
                rrcId + "&login=" +
                rrcLogin + "&pass=" +
                rrcPassword + "\"";
        String downloadCommand = "wget \"https://b2b.rrc.ru/personal/xml/HK5NqWYgRFwC7yOuEkA3IBUa9Sprs0e1.xml\"";

        System.out.println(generateCommand);

        List<String> commands = new ArrayList<>();
        commands.add(generateCommand);
        commands.add(downloadCommand);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        processBuilder.directory(new File("D:/NewFolder/"));

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(generateCommand);
//            process = processBuilder.start();
//            InputStream inputStream = process.getInputStream();
//            System.out.println(Arrays.toString(inputStream.readAllBytes()));

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            process.destroy();

            process = Runtime.getRuntime().exec(downloadCommand);
            process.waitFor();
//            process.destroy();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
//        int exitCode = process.exitValue();

        System.out.println(process.exitValue());

        System.out.println(process);

        process.destroy();

//        System.out.println(command);
    }
}
