package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileServiceImpl implements FileService {
    private final Bot bot;
    private final PriceServiceImpl priceServiceImpl;
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public FileServiceImpl(Bot bot, PriceServiceImpl priceServiceImpl) {
        this.bot = bot;
        this.priceServiceImpl = priceServiceImpl;
    }

    @Override
    public void getFile(Update update) {
        if (update.getMessage().getDocument().getFileName().equals("GPL.zip")) {
            downloadGPL(update);
        } else {
            //@TODO Сделать эксепшен
            bot.sendMessage(update.getMessage().getChatId().toString(), "Неверный формат файла");
        }
    }

    private void downloadGPL(Update update) {
        try {
            URL url = new URL("https://api.telegram.org/bot" +
                    bot.getBotToken() + "/getFile?file_id=" +
                    update.getMessage().getDocument().getFileId());
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String fileResponse = br.readLine();
            br.close();

            JSONObject result = new JSONObject(fileResponse);
            JSONObject path = result.getJSONObject("result");
            String filePath = path.getString("file_path");

            try(ZipInputStream zis = new ZipInputStream(new URL("https://api.telegram.org/file/bot" +
                    bot.getBotToken() + "/" + filePath).openStream())) {
                ZipEntry entry;
                String name;
                while((entry = zis.getNextEntry()) != null) {
                    name = entry.getName();
//                    File file = new File("src/main/resources/upload/" + name);
                    File file = new File(uploadPath + "/" + name);


                    FileOutputStream fos = new FileOutputStream(file);
                    for (int c = zis.read(); c != -1; c = zis.read()) {
                        fos.write(c);
                    }
                    fos.flush();
                    zis.closeEntry();
                    fos.close();
//                    System.out.println("END");

                    String resp = priceServiceImpl.uploadGPL(file);
                    file.delete();

                    bot.sendMessage(update.getMessage().getChatId().toString(), resp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
