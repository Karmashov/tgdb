package com.wwd.tgdb.scheduled;

import lombok.Data;

import java.io.File;
import java.io.IOException;
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
//        String downloadCommand = "wget -O gpl.xml https://b2b.rrc.ru/personal/xml/HK5NqWYgRFwC7yOuEkA3IBUa9Sprs0e1.xml";
        String downloadCommand = "wget https://b2b.rrc.ru/personal/xml/HK5NqWYgRFwC7yOuEkA3IBUa9Sprs0e1.xml";


        System.out.println(generateCommand);
        System.out.println(downloadCommand);


        List<String> commands = new ArrayList<>();
        commands.add(generateCommand);

//        commands.add(downloadCommand);

        ProcessBuilder processBuilder = new ProcessBuilder(generateCommand.split(" "));
        ProcessBuilder processBuilder1 = new ProcessBuilder(downloadCommand.split(" "));

//        processBuilder.directory(new File("D:/NewFolder/"));
        processBuilder.redirectInput(new File("D:/NewFolder/1.txt"));
        processBuilder1.redirectInput(new File("D:/NewFolder/gpl.xml"));
//        processBuilder.redirectOutput(new File("D:/NewFolder/1.txt"));

        Process process = null;
        Process process1 = null;
        try {
//            process = Runtime.getRuntime().exec(generateCommand);
            process = processBuilder.start();
//            InputStream inputStream = process.getInputStream();
//            System.out.println(Arrays.toString(inputStream.readAllBytes()));

//            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
            process.waitFor();

            process1 = processBuilder1.start();
            process1.waitFor();
//            process.destroy();

//            process = Runtime.getRuntime().exec(downloadCommand);
//            process.waitFor();
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
