package com.marmitas.entregademarmitas.service;

import com.marmitas.entregademarmitas.model.Cliente;
import com.marmitas.entregademarmitas.model.Retirada;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {
    
    private static final String[] HEADERS = {"ID", "Nome", "Endereço", "RG", "Data", "Recebeu"};
    private static final String[] RETIRADA_HEADERS = {"ID Retirada", "Nome Cliente", "Código Cliente", "Data/Hora Retirada"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public byte[] exportClientesToExcel(List<Cliente> clientes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Clientes");
            
            // Criar estilo para o cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Criar estilo para os dados
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setWrapText(true);
            
            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Preencher dados
            int rowNum = 1;
            for (Cliente cliente : clientes) {
                Row row = sheet.createRow(rowNum++);
                
                // ID
                Cell idCell = row.createCell(0);
                idCell.setCellValue(cliente.getId());
                idCell.setCellStyle(dataStyle);
                
                // Nome
                Cell nomeCell = row.createCell(1);
                nomeCell.setCellValue(cliente.getNome());
                nomeCell.setCellStyle(dataStyle);
                
                // Endereço
                Cell enderecoCell = row.createCell(2);
                enderecoCell.setCellValue(cliente.getEndereco());
                enderecoCell.setCellStyle(dataStyle);
                
                // RG
                Cell rgCell = row.createCell(3);
                rgCell.setCellValue(cliente.getRg() != null ? cliente.getRg() : "");
                rgCell.setCellStyle(dataStyle);
                
                // Data
                Cell dataCell = row.createCell(4);
                dataCell.setCellValue(cliente.getData().format(DATE_FORMATTER));
                dataCell.setCellStyle(dataStyle);
                
                // Recebeu
                Cell recebeuCell = row.createCell(5);
                recebeuCell.setCellValue(cliente.getRecebeu() ? "Sim" : "Não");
                recebeuCell.setCellStyle(dataStyle);
            }
            
            // Auto-ajustar largura das colunas
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Ajuste adicional para garantir que o conteúdo caiba
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 4000));
            }
            
            // Criar rodapé com estatísticas
            Row footerRow = sheet.createRow(rowNum + 2);
            Cell totalLabelCell = footerRow.createCell(0);
            totalLabelCell.setCellValue("Total de Clientes:");
            totalLabelCell.setCellStyle(headerStyle);
            
            Cell totalValueCell = footerRow.createCell(1);
            totalValueCell.setCellValue(clientes.size());
            totalValueCell.setCellStyle(dataStyle);
            
            Cell receberamLabelCell = footerRow.createCell(3);
            receberamLabelCell.setCellValue("Receberam:");
            receberamLabelCell.setCellStyle(headerStyle);
            
            long receberamCount = clientes.stream().mapToLong(c -> c.getRecebeu() ? 1 : 0).sum();
            Cell receberamValueCell = footerRow.createCell(4);
            receberamValueCell.setCellValue(receberamCount);
            receberamValueCell.setCellStyle(dataStyle);
            
            Cell naoReceberamLabelCell = footerRow.createCell(5);
            naoReceberamLabelCell.setCellValue("Não Receberam:");
            naoReceberamLabelCell.setCellStyle(headerStyle);
            
            long naoReceberamCount = clientes.size() - receberamCount;
            Cell naoReceberamValueCell = footerRow.createCell(6);
            naoReceberamValueCell.setCellValue(naoReceberamCount);
            naoReceberamValueCell.setCellStyle(dataStyle);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportRetiradasToExcel(List<Retirada> retiradas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Retiradas de Marmitas");
            
            // Criar estilo para o cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Criar estilo para os dados
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setWrapText(true);
            
            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < RETIRADA_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(RETIRADA_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Preencher dados
            int rowNum = 1;
            for (Retirada retirada : retiradas) {
                Row row = sheet.createRow(rowNum++);
                
                // ID Retirada
                Cell idCell = row.createCell(0);
                idCell.setCellValue(retirada.getId());
                idCell.setCellStyle(dataStyle);
                
                // Nome Cliente
                Cell nomeCell = row.createCell(1);
                nomeCell.setCellValue(retirada.getCliente().getNome());
                nomeCell.setCellStyle(dataStyle);
                
                // Código Cliente
                Cell codigoCell = row.createCell(2);
                codigoCell.setCellValue(retirada.getCliente().getId());
                codigoCell.setCellStyle(dataStyle);
                
                // Data/Hora Retirada
                Cell dataCell = row.createCell(3);
                dataCell.setCellValue(retirada.getDataRetirada().format(DATETIME_FORMATTER));
                dataCell.setCellStyle(dataStyle);
            }
            
            // Auto-ajustar largura das colunas
            for (int i = 0; i < RETIRADA_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Ajuste adicional para garantir que o conteúdo caiba
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 5000));
            }
            
            // Criar rodapé com estatísticas
            Row footerRow = sheet.createRow(rowNum + 2);
            Cell totalLabelCell = footerRow.createCell(0);
            totalLabelCell.setCellValue("Total de Retiradas:");
            totalLabelCell.setCellStyle(headerStyle);
            
            Cell totalValueCell = footerRow.createCell(1);
            totalValueCell.setCellValue(retiradas.size());
            totalValueCell.setCellStyle(dataStyle);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
