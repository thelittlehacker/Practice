package com.example.machinetest.controller;

import com.example.machinetest.payload.Response;
import com.example.machinetest.service.FileStorageService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ExcelHandleController {
    @Autowired
    private FileStorageService fileStorageService;
    private List<String> colName = new ArrayList<>();
    private List<String> WorkBooksheetName = new ArrayList<>();
    private List<String> sheetCellValue = new ArrayList<>();
    private Workbook workbook;
    private List<Workbook> listOfWorkBook = new ArrayList<>();
    private List<String> listFilename = new ArrayList<>();
    private List<List<String>> listColumnname = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(ExcelHandleController.class);


    @RequestMapping("/")
    public String getUploadPage()
    {
        return "uploadExcel";
    }


    @PostMapping("/download")
    public ResponseEntity<Resource> uploadExcel(@RequestParam("columnName") String columnName[],
                                                @RequestParam("filetype") String filetype,
                                                @RequestParam("delimiter") String delimiter, HttpServletRequest request) throws IOException {
        Resource resource = null;

        List<List<String>> columnValuebyPassingSheetNameAndColumnName = getColumnValuebyPassingSheetNameAndColumnName
                (listOfWorkBook,
                columnName,
                filetype,
                delimiter);
        resource = getXlsx(columnValuebyPassingSheetNameAndColumnName, filetype, delimiter);


        workbook.close();

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }
        if(contentType == null) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        listFilename.clear();
        listColumnname.clear();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);


    }


    @PostMapping("/uploadMultipleFiles")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, ModelMap modelMap) {

         Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file, modelMap))
                .collect(Collectors.toList());

        return "downloadExcel";
    }


    public String uploadFile(@RequestParam("file") MultipartFile file, ModelMap modelMap) {

        String fileName = fileStorageService.storeFile(file);
        if (!colName.isEmpty())
        {
            colName = new ArrayList<>();
        }
        try {
             excelHandle(file);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        listFilename.add(fileName);
        listColumnname.add(colName);
        modelMap.addAttribute("filename", listFilename);
        modelMap.addAttribute("columnname", listColumnname);
        return "downloadExcel";
    }


    public void excelHandle(MultipartFile file) throws IOException, InvalidFormatException {

        final Workbook workbook = openWorkBook(file);
        listOfWorkBook.add(workbook);
        for (int j = 0; j < workbook.getNumberOfSheets() ; j++) {
            Sheet  sheet = workbook.getSheetAt(j);
            if (sheet.getRow(1)!= null){
                getColumnName(sheet);
            }
        }
        workbook.close();
    }

    public Workbook openWorkBook(MultipartFile file) throws IOException, InvalidFormatException {
        workbook = WorkbookFactory.create(new File(fileStorageService.loadFileAsResource(file.getOriginalFilename())));
        return workbook;
    }


    public List<String> getColumnName(Sheet excelSheet)
    {
        if (excelSheet.getRow(1) != null) {
            int colNum = excelSheet.getRow(0).getLastCellNum();
            for (int i = 0; i <colNum ; i++) {
              String excelColName = excelSheet.getRow(0).getCell(i).getRichStringCellValue().toString();
                colName.add(excelColName);
            }
        }
        return colName;

    }

    public List<List<String>> getColumnValuebyPassingSheetNameAndColumnName(List<Workbook> listwrk, String[] colname, String filettype, String delimiter) throws IOException {
        Map<Object, String> data = new HashMap<>();
        List<List<String>> colValue = new ArrayList<>();

        for(Workbook wrk : listwrk) {
            data.clear();
           for (int j = 0; j < wrk.getNumberOfSheets(); j++) {
                Sheet sheet = wrk.getSheetAt(j);


                if (sheet.getRow(1) != null) {
                    for (String columnName : colname) {
                        int rowNum = sheet.getLastRowNum() + 1;
                        int colNum = sheet.getRow(0).getLastCellNum();
                        Row row = null;
                        Cell cell = null;

                        Map<String, Integer> colMapByName = new HashMap<>();
                        if (sheet.getRow(0).cellIterator().hasNext()) {
                            for (int x = 0; x < colNum; x++) {
                                colMapByName.put(String.valueOf((sheet.getRow(0).getCell(x))), x);
                            }
                        }

                        List<String> rowData = new ArrayList<>();
                        for (int i = 0; i < rowNum; i++) {
                            row = sheet.getRow(i);
                            for (Map.Entry m : colMapByName.entrySet()) {
                                cell = row.getCell(colMapByName.get(m.getKey()));
                                data.put(m.getKey(), String.valueOf(cell));
                            }
                            if (data.get(columnName) != null){
                                rowData.add(data.get(columnName));
                                }
                        }
                        if(!rowData.isEmpty()) {
                            colValue.add(rowData);
                        }
                    }
                }
            }
        }
        workbook.close();
        return colValue;
    }

    public Resource getXlsx(List<List<String>> columnValue, String filetype, String delimitor) throws IOException {

        Resource resource;
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("New Fields");

        int cellIndex = 0;
        for(List<String> c : columnValue)
        {
            int rowIndex = 0;
            for (String a : c){
                Row row = sheet.getRow(rowIndex);
                if(row == null) {
                    row = sheet.createRow(rowIndex);
                }
                row.createCell(cellIndex).setCellValue(a);

                rowIndex++;
            }
            cellIndex++;
        }

        File myFile = new File("RevisedExcelSheet.xlsx");
        FileOutputStream fileOut = new FileOutputStream(myFile);
        Path path = Paths.get(myFile.getPath());
        resource = new UrlResource(path.toUri());
        workbook.write(fileOut);
        fileOut.close();

        if (filetype.equals("csv")){
            resource = getCSV(workbook);
        }
        if (filetype.equals("txt")){
            resource = getText(workbook, delimitor);
        }
        workbook.close();
        return resource;
    }



    public Resource getCSV (Workbook wb) throws IOException {
        DataFormatter formatter = new DataFormatter();
        File myFile = new File("test.csv");
        PrintStream out = new PrintStream(new FileOutputStream(myFile),
                true, "UTF-8");
        for (Sheet sheet : wb) {
            for (Row row : sheet) {
                boolean firstCell = true;
                for (Cell cell : row) {
                    if (!firstCell) out.print(',');
                    String text = formatter.formatCellValue(cell);
                    out.print(text);
                    firstCell = false;
                }
                out.println();
            }
        }
        Path path = Paths.get(myFile.getPath());
        Resource resource = new UrlResource(path.toUri());
        out.close();
        wb.close();
        return resource;
    }

    public Resource getText(Workbook wb, String delimitor) throws IOException {

        DataFormatter formatter = new DataFormatter();
        File myFile = new File("newTextfile.txt");
        PrintStream out = new PrintStream(new FileOutputStream(myFile),
                true, "UTF-8");

        for (Sheet sheet : wb) {
            for (Row row : sheet) {
                boolean firstCell = true;
                for (Cell cell : row) {
                    if (!firstCell) out.print(delimitor);
                    String text = formatter.formatCellValue(cell);
                    out.print(text);
                    firstCell = false;
                }
                out.println();
            }
        }
        Path path = Paths.get(myFile.getPath());
        Resource resource = new UrlResource(path.toUri());
        out.close();
        wb.close();
        return resource;
    }





 //---------------------------------Some other Methods------------------------------//

    public List<String> getSheetName(Workbook workBooksheetName) {
        workBooksheetName.forEach(sheet -> {
            final String sheetName = sheet.getSheetName();
            WorkBooksheetName.add(sheetName);
        });
        return WorkBooksheetName;
    }

    public void readingSheet(Workbook readSheetRowandColumn) throws IOException {
        for (int j = 0; j < readSheetRowandColumn.getNumberOfSheets() ; j++) {
            Sheet sheet = readSheetRowandColumn.getSheetAt(j);

            DataFormatter dataFormatter = new DataFormatter();

            if (sheet.getRow(1)!= null){

                sheet.forEach(row -> {
                    row.forEach(cell -> {
                        String cellValue = dataFormatter.formatCellValue(cell);
                        sheetCellValue.add(cellValue);
                        System.out.print(cellValue + "\t");
                    });
                    System.out.println();

                });
                readSheetRowandColumn.close();
            }
        }
    }
}


