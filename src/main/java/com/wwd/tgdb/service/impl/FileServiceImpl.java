package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.dto.Response;
import com.wwd.tgdb.exception.IllegalFormatException;
import com.wwd.tgdb.model.GPL.RusSO;
import com.wwd.tgdb.repository.PriceRepository;
import com.wwd.tgdb.repository.RusSORepository;
import com.wwd.tgdb.scheduled.ScheduledTasks;
import com.wwd.tgdb.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileServiceImpl implements FileService {
    private final Bot bot;
    private final ScheduledTasks tasks;
    private final PriceServiceImpl priceServiceImpl;
    private final PriceRepository priceRepository;
    private final RusSORepository soRepository;
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public FileServiceImpl(Bot bot,
                           ScheduledTasks tasks,
                           PriceServiceImpl priceServiceImpl,
                           PriceRepository priceRepository,
                           RusSORepository soRepository) {
        this.bot = bot;
        this.tasks = tasks;
        this.priceServiceImpl = priceServiceImpl;
        this.priceRepository = priceRepository;
        this.soRepository = soRepository;
    }

    @Override
    public void getFile(Update update) throws IllegalFormatException {
        String fileName = update.getMessage().getDocument().getFileName();

        Response response;
        if (fileName.equals("GPL.zip")) {
            response = priceServiceImpl.uploadGPL(downloadFile(update));
        } else if (fileName.startsWith("SO") && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))){
            response = uploadSO(downloadFile(update));
        } else {
            throw new IllegalFormatException();
        }

        response.setChatId(update.getMessage().getChatId());
        response.setMessageId(update.getMessage().getMessageId());
        bot.sendReply(response);
    }

    private Response uploadSO(File file) {
        int count = 0;
        try {
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            int rowIndex = 0;
            int modelCellIndex = 0;
            int serialCellIndex = 0;
            int customerCellIndex = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                if (row.getCell(0).toString().startsWith("Product Number")) {
                    rowIndex = row.getRowNum();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (cell.toString()) {
                            case "Product Number":
                                modelCellIndex = cell.getColumnIndex();
                                break;
                            case "PAK/Serial Number":
                                serialCellIndex = cell.getColumnIndex();
                                break;
                            case "End Customer GU Name":
                                customerCellIndex = cell.getColumnIndex();
                                break;
                        }
                    }
                }

                if (row.getRowNum() > rowIndex && rowIndex != 0) {
                    String partnumber = "";
                    String serial = "";
                    String customer = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (cell.getColumnIndex() == modelCellIndex) {
                            partnumber = cell.toString();
                        } else if (cell.getColumnIndex() == serialCellIndex) {
                            serial = cell.toString();
                        } else if (cell.getColumnIndex() == customerCellIndex) {
                            customer = cell.toString();
                        }
                    }
                    if (!serial.isEmpty() && priceRepository.existsByPartnumber(partnumber) && !soRepository.existsBySerial(serial)) {
                        RusSO data = new RusSO();
                        data.setPartnumber(priceRepository.findFirstByPartnumber(partnumber));
                        data.setSerial(serial);
                        data.setCustomer(customer);
                        soRepository.save(data);
                        count++;
                    }
                }
            }
            file.delete();
            return new Response("Файл загружен.\n" +
                    "Внесено " + count + " строк.");
        } catch (IOException | InvalidFormatException exception) {
            exception.printStackTrace();
        }
        return new Response("Файл не загружен");
    }

    private File downloadFile(Update update) {
        File file = null;
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

            String fileName = update.getMessage().getDocument().getFileName();
            URL targetFile = new URL("https://api.telegram.org/file/bot" +
                    bot.getBotToken() + "/" + filePath);
            if (fileName.endsWith(".zip")) {
                try (ZipInputStream zis = new ZipInputStream(targetFile.openStream())) {
                    ZipEntry entry;
                    String name;
                    while((entry = zis.getNextEntry()) != null) {
                        name = entry.getName();
//                    File file = new File("src/main/resources/upload/" + name);
                        file = new File(uploadPath + "/" + name);


                        FileOutputStream fos = new FileOutputStream(file);
                        for (int c = zis.read(); c != -1; c = zis.read()) {
                            fos.write(c);
                        }
                        fos.flush();
                        zis.closeEntry();
                        fos.close();
                    }
                }
            } else {
                file = new File(uploadPath + "/" + fileName);
                FileUtils.copyURLToFile(targetFile, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Scheduled(fixedDelay = 300_000)
//    @Scheduled(fixedDelay = 5000)
    public void scheduledTask() {
//		ScheduledTasks tasks = new ScheduledTasks();

        System.out.println(LocalDateTime.now());
        tasks.getGpl();
    }
}
