package rampup;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

public class HospitalDataExcelHandler {
    private Workbook workbook;
    private Sheet sheet;
    private String filename; 

    public HospitalDataExcelHandler(String filename) {
        this.filename = filename;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Hospital Data");

        // Create header row for hospital data
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Day");
        headerRow.createCell(1).setCellValue("Number of Admissions");
        headerRow.createCell(2).setCellValue("Number of Rejections");
        headerRow.createCell(3).setCellValue("Bed Occupancy Rate");
        headerRow.createCell(4).setCellValue("Performance Score");
        headerRow.createCell(5).setCellValue("Capacity");
        headerRow.createCell(6).setCellValue("Number of active Patients");

        // Create header row for zone info
        Row zoneInfoHeaderRow = sheet.createRow(1);
        zoneInfoHeaderRow.createCell(0).setCellValue("Zone");
        zoneInfoHeaderRow.createCell(1).setCellValue("Proportion");
        
        
        // Create header row for Patients info
        Row patientInfoHeaderRow = sheet.createRow(2);
        patientInfoHeaderRow.createCell(0).setCellValue("dailyPatient");
        saveToFile();

    }

    public void addHospitalData(int day, int numAdmissions, int numRejections, int bedOccupancyRate, double performanceScore,
            int capacity, int activePatients) {
        int currentRow = sheet.getLastRowNum() + 1;
        Row newRow = sheet.createRow(currentRow);
        newRow.createCell(0).setCellValue(day);
        newRow.createCell(1).setCellValue(numAdmissions);
        newRow.createCell(2).setCellValue(numRejections);
        newRow.createCell(3).setCellValue(bedOccupancyRate);
        newRow.createCell(4).setCellValue(performanceScore);
        newRow.createCell(5).setCellValue(capacity);
        newRow.createCell(6).setCellValue(activePatients);
        saveToFile();

    }

    public void addZoneInfo(int zone, double proportion) {
        int currentRow = sheet.getLastRowNum() + 1;
        Row newRow = sheet.createRow(currentRow);
        newRow.createCell(0).setCellValue(zone);
        newRow.createCell(1).setCellValue(proportion);
        saveToFile();

    }
    
    public void addpatientInfo(int dailyPatient) {
    	
        int currentRow = sheet.getLastRowNum() + 1;
        Row newRow = sheet.createRow(currentRow);
        newRow.createCell(0).setCellValue(dailyPatient);

        saveToFile();
    	
    }

    public void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(filename)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
