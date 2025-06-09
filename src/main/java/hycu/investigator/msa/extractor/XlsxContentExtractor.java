package hycu.investigator.msa.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxContentExtractor implements ContentExtractor {

    @Override
    public String extract(Path filePath) throws IOException {
        System.out.println("XLSX 텍스트 추출 중 (Apache POI): " + filePath);
        StringBuilder content = new StringBuilder();
        try (InputStream is = Files.newInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        // 셀 타입에 따라 값 가져오기
                        switch (cell.getCellType()) {
                            case STRING:
                                content.append(cell.getStringCellValue()).append(" ");
                                break;
                            case NUMERIC:
                                content.append(cell.getNumericCellValue()).append(" ");
                                break;
                            case BOOLEAN:
                                content.append(cell.getBooleanCellValue()).append(" ");
                                break;
                            case FORMULA:
                                // 수식 셀의 경우 계산된 값을 가져오거나 수식 자체를 가져올 수 있음
                                try {
                                    content.append(cell.getNumericCellValue()).append(" "); // 계산된 숫자 값 시도
                                } catch (IllegalStateException e) {
                                    content.append(cell.getStringCellValue()).append(" "); // 문자열 값 시도
                                }
                                break;
                            default:
                                // 다른 타입 (BLANK, ERROR)은 무시
                                break;
                        }
                    }
                    content.append("\n"); // 한 행 끝날 때마다 줄 바꿈
                }
            }
            String extractedText = content.toString();
            System.out.println("XLSX 텍스트 추출 완료. (추출된 텍스트 크기: " + extractedText.length() + " 바이트)");
            return extractedText;
        }
    }

    @Override
    public boolean supports(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        return fileName.endsWith(".xlsx");
        // 또는 Files.probeContentType(filePath)를 사용하여 "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" 확인
    }
}
