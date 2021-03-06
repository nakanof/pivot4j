/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 */
package org.pivot4j.ui.poi;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.olap4j.Axis;
import org.pivot4j.PivotException;
import org.pivot4j.ui.AbstractContentRenderCallback;
import org.pivot4j.ui.command.UICommand;
import org.pivot4j.ui.table.TableRenderCallback;
import org.pivot4j.ui.table.TableRenderContext;

import static org.pivot4j.ui.CellTypes.LABEL;
import static org.pivot4j.ui.CellTypes.VALUE;

public class ExcelExporter extends
        AbstractContentRenderCallback<TableRenderContext> implements
        TableRenderCallback {

    private Format format = Format.HSSF;

    private Workbook workbook;

    private Sheet sheet;

    private Row row;

    private Cell cell;

    private CellStyle headerCellStyle;

    private CellStyle valueCellStyle;

    private CellStyle aggregationCellStyle;

    private List<CellRangeAddress> mergedRegions;

    private String fontFamily = "Arial";

    private int fontSize = 10;

    private int sheetIndex = 0;

    private Map<String, CellStyle> styleCache = new HashMap<String, CellStyle>();

    /**
     * @param out
     */
    public ExcelExporter(OutputStream out) {
        super(out);
    }

    /**
     * @see org.pivot4j.ui.AbstractRenderCallback#getContentType()
     */
    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    /**
     * @return the format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * @return the fontFamily
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * @param fontFamily the fontFamily to set
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * @return the fontSize
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @see
     * org.pivot4j.ui.RenderCallback#startRender(org.pivot4j.ui.RenderContext)
     */
    @Override
    public void startRender(TableRenderContext context) {
        this.sheet = null;
        this.row = null;
        this.cell = null;
        this.mergedRegions = new ArrayList<CellRangeAddress>();
        this.sheetIndex = 0;

        this.headerCellStyle = null;
        this.valueCellStyle = null;

        this.workbook = createWorkbook();
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#startTable(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void startTable(TableRenderContext context) {
        this.sheet = createSheet(context, workbook);
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#startHeader(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void startHeader(TableRenderContext context) {
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#endHeader(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void endHeader(TableRenderContext context) {
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#startBody(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void startBody(TableRenderContext context) {
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#startRow(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void startRow(TableRenderContext context) {
        this.row = createRow(sheet, context.getRowIndex() + getRowOffset());
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#startCell(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void startCell(TableRenderContext context) {
        this.cell = createCell(row, context.getColumnIndex() + getColOffset());
    }

    /**
     * @see
     * org.pivot4j.ui.RenderCallback#renderCommands(org.pivot4j.ui.RenderContext,
     * java.util.List)
     */
    @Override
    public void renderCommands(TableRenderContext context,
            List<UICommand<?>> commands) {
    }

    /**
     * @see
     * org.pivot4j.ui.RenderCallback#renderContent(org.pivot4j.ui.RenderContext,
     * java.lang.String, java.lang.Double)
     */
    @Override
    public void renderContent(TableRenderContext context, String label,
            Double value) {
        cell.setCellStyle(getCellStyle(context));

        if (VALUE.equals(context.getCellType())
                && context.getAxis() != Axis.FILTER) {
            if (value == null) {
                cell.setCellValue("");
            } else if (label.length() > 2 && label.charAt(0) == '|') {
                StringTokenizer st = new StringTokenizer(label, "|");
                cell.setCellValue(st.nextToken());
                String styleName = st.nextToken();
                CellStyle style = styleCache.get(styleName);

                if (style == null) {
                    Map<String, String> attrs = parseStyle(styleName);

                    style = format == Format.HSSF
                            ? createHSSFStyle(attrs) : createXSSFStyle(attrs);
                    styleCache.put(styleName, style);
                }
                cell.setCellStyle(style);
                cell.setCellType(Cell.CELL_TYPE_STRING);
            } else {
                cell.setCellValue(value);
            }
        } else {
            boolean showParentMembers = context.getRenderer()
                    .getShowParentMembers();

            if (!showParentMembers && label != null
                    && context.getAxis() == Axis.ROWS
                    && context.getMember() != null && context.getCell() == null) {
                label = StringUtils.leftPad(label, context.getMember()
                        .getDepth() + label.length());
            }

            cell.setCellValue(label);
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
    }

    private CellStyle createHSSFStyle(Map<String, String> attrs) {
        HSSFCellStyle style = (HSSFCellStyle) workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        HSSFFont font = null;

        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            if ("color".equalsIgnoreCase(entry.getKey())) {
                Color rgb = fromHtmlColor(entry.getValue());
                if (font == null) {
                    font = (HSSFFont) workbook.createFont();
                }
                HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
                HSSFColor color = palette.findSimilarColor(
                        rgb.getRed(), rgb.getGreen(), rgb.getBlue());

                if (color == null) {
                    color = palette.addColor(
                            (byte) rgb.getRed(), (byte) rgb.getGreen(), (byte) rgb.getBlue());
                }

                font.setColor(color.getIndex());
            } else if ("font-style".equalsIgnoreCase(entry.getKey())) {
                if (font == null) {
                    font = (HSSFFont) workbook.createFont();
                }
                font.setItalic("italic".equalsIgnoreCase(entry.getValue()));
            } else if ("font-weight".equalsIgnoreCase(entry.getKey())) {
                if (font == null) {
                    font = (HSSFFont) workbook.createFont();
                }
                boolean weight = "bold".equalsIgnoreCase(entry.getValue());
                font.setBoldweight((short) (weight ? 700 : 400));
            } else if ("border".equalsIgnoreCase(entry.getKey())) {
                /* ???? */
            }
        }

        if (font != null) {
            style.setFont(font);
        }

        return style;
    }

    private CellStyle createXSSFStyle(Map<String, String> attrs) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());

        XSSFFont font = null;

        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            if ("color".equalsIgnoreCase(entry.getKey())) {
                Color rgb = fromHtmlColor(entry.getValue());
                if (font == null) {
                    font = (XSSFFont) workbook.createFont();
                }
                font.setColor(new XSSFColor(rgb));
            } else if ("font-style".equalsIgnoreCase(entry.getKey())) {
                if (font == null) {
                    font = (XSSFFont) workbook.createFont();
                }
                font.setItalic("italic".equalsIgnoreCase(entry.getValue()));
            } else if ("font-weight".equalsIgnoreCase(entry.getKey())) {
                if (font == null) {
                    font = (XSSFFont) workbook.createFont();
                }
                font.setBold("bold".equalsIgnoreCase(entry.getValue()));
            } else if ("border".equalsIgnoreCase(entry.getKey())) {
                /* ???? */
            }
        }

        if (font != null) {
            style.setFont(font);
        }

        return style;
    }

    private static Map<String, String> parseStyle(String style) {
        Map<String, String> attributes = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(style, ":;");

        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            if (st.hasMoreTokens()) {
                attributes.put(key, st.nextToken());
            }
        }

        return attributes;
    }

    private static Color fromHtmlColor(String color) {
        if (color == null) {
            throw new NullPointerException(
                    "The color components or name must be specified");
        }
        if (color.isEmpty()) {
            throw new IllegalArgumentException("Invalid color specification");
        }

        if (color.startsWith("#")) {
            color = color.substring("#".length());
        }

        int len = color.length();

        try {
            int r;
            int g;
            int b;

            if (len < 6) {
                r = Integer.parseInt(color.substring(0, 1), 16);
                g = Integer.parseInt(color.substring(1, 2), 16);
                b = Integer.parseInt(color.substring(2, 3), 16);
                return new Color(r * 16, g * 16, b * 16);
            } else {
                r = Integer.parseInt(color.substring(0, 2), 16);
                g = Integer.parseInt(color.substring(2, 4), 16);
                b = Integer.parseInt(color.substring(4, 6), 16);
                return new Color(r, g, b);
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid color specification");
        }
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#endCell(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void endCell(TableRenderContext context) {
        this.cell = null;

        CellRangeAddress range = createMergedRegion(context);
        if (range != null) {
            mergedRegions.add(range);
        }
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#endRow(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void endRow(TableRenderContext context) {
        this.row = null;
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#endBody(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void endBody(TableRenderContext context) {
    }

    /**
     * @see
     * org.pivot4j.ui.table.TableRenderCallback#endTable(org.pivot4j.ui.table.TableRenderContext)
     */
    @Override
    public void endTable(TableRenderContext context) {
        mergeCells(context, sheet, mergedRegions);
        adjustColumnSizes(context, sheet);

        mergedRegions.clear();

        this.sheet = null;
    }

    /**
     * @see
     * org.pivot4j.ui.RenderCallback#endRender(org.pivot4j.ui.RenderContext)
     */
    @Override
    public void endRender(TableRenderContext context) {
        try {
            workbook.write(getOutputStream());
        } catch (IOException e) {
            throw new PivotException(e);
        }
    }

    protected Workbook createWorkbook() {
        Workbook newWorkbook = null;

        switch (format) {
            case XSSF:
                newWorkbook = new XSSFWorkbook();
                break;
            case HSSF:
                newWorkbook = new HSSFWorkbook();
                break;
            case SXSSF:
                newWorkbook = new SXSSFWorkbook(65535);
                break;
            default:
                assert false;
        }

        return newWorkbook;
    }

    /**
     * @return workbook
     */
    protected Workbook getWorkbook() {
        return workbook;
    }

    /**
     * @param context
     * @param workbook
     * @return
     */
    protected Sheet createSheet(TableRenderContext context, Workbook workbook) {
        Sheet newSheet = workbook.createSheet(WorkbookUtil
                .createSafeSheetName(getSheetName(context, sheetIndex)));

        sheetIndex++;
        return newSheet;
    }

    /**
     * @param context
     * @param sheetIndex
     * @return
     */
    protected String getSheetName(TableRenderContext context, int sheetIndex) {
        String name;

        if (context.getAxis() == Axis.FILTER) {
            name = context.getResourceBundle().getString("label.filter")
                    + " - " + context.getHierarchy().getCaption();
        } else {
            name = context.getModel().getCube().getCaption();
        }

        return name;
    }

    /**
     * @return sheet
     */
    protected Sheet getSheet() {
        return sheet;
    }

    protected int getRowOffset() {
        return 0;
    }

    protected int getColOffset() {
        return 0;
    }

    /**
     * @param sheet
     * @param rowIndex
     * @return
     */
    protected Row createRow(Sheet sheet, int rowIndex) {
        return sheet.createRow(rowIndex);
    }

    /**
     * @return row
     */
    protected Row getRow() {
        return row;
    }

    /**
     * @param row
     * @param colIndex
     * @return
     */
    protected Cell createCell(Row row, int colIndex) {
        return row.createCell(colIndex);
    }

    /**
     * @return cell
     */
    protected Cell getCell() {
        return cell;
    }

    protected CellStyle createHeaderCellStyle() {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();

        font.setFontName(fontFamily);
        font.setFontHeightInPoints((short) fontSize);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        switch (format) {
            case XSSF:
            case SXSSF:
                ((XSSFCellStyle) style).setFillForegroundColor(new XSSFColor(
                        Color.lightGray));
                break;
            case HSSF:
                style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                break;
            default:
                assert false;
        }

        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);

        return style;
    }

    /**
     * @return headerCellStyle
     */
    protected CellStyle getHeaderCellStyle() {
        if (headerCellStyle == null) {
            this.headerCellStyle = createHeaderCellStyle();
        }

        return headerCellStyle;
    }

    protected CellStyle createValueCellStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setFontName(fontFamily);
        font.setFontHeightInPoints((short) fontSize);
        font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setDataFormat((short) 4);

        return style;
    }

    /**
     * @return valueCellStyle
     */
    protected CellStyle getValueCellStyle() {
        if (valueCellStyle == null) {
            this.valueCellStyle = createValueCellStyle();
        }

        return valueCellStyle;
    }

    protected CellStyle createAggregationCellStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setFontName(fontFamily);
        font.setFontHeightInPoints((short) fontSize);
        font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        switch (format) {
            case XSSF:
            case SXSSF:
                ((XSSFCellStyle) style).setFillForegroundColor(new XSSFColor(
                        Color.lightGray));
                break;
            case HSSF:
                style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                break;
            default:
                assert false;
        }

        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setDataFormat((short) 4);

        return style;
    }

    /**
     * @return aggregationCellStyle
     */
    protected CellStyle getAggregationCellStyle() {
        if (aggregationCellStyle == null) {
            this.aggregationCellStyle = createAggregationCellStyle();
        }

        return aggregationCellStyle;
    }

    /**
     * @param context
     * @return
     */
    protected CellStyle getCellStyle(TableRenderContext context) {
        if (VALUE.equals(context.getCellType())) {
            if (context.getAggregator() == null) {
                return getValueCellStyle();
            } else {
                return getAggregationCellStyle();
            }
        } else if (LABEL.equals(context.getCellType())
                && context.getAxis() == Axis.FILTER) {
            return getValueCellStyle();
        } else {
            return getHeaderCellStyle();
        }
    }

    /**
     * @param context
     * @return
     */
    protected CellRangeAddress createMergedRegion(TableRenderContext context) {
        if (context.getColumnSpan() > 1 || context.getRowSpan() > 1) {
            int firstRow = context.getRowIndex() + getRowOffset();
            int lastRow = firstRow + context.getRowSpan() - 1;
            int firstCol = context.getColumnIndex() + getColOffset();
            int lastCol = firstCol + context.getColumnSpan() - 1;

            return new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        } else {
            return null;
        }
    }

    /**
     * @param context
     * @param sheet
     * @param regions
     */
    protected void mergeCells(TableRenderContext context, Sheet sheet,
            List<CellRangeAddress> regions) {
        for (CellRangeAddress region : regions) {
            sheet.addMergedRegion(region);

            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region, sheet,
                    workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region, sheet,
                    workbook);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region, sheet,
                    workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region, sheet,
                    workbook);
        }
    }

    /**
     * @param context
     * @param sheet
     */
    protected void adjustColumnSizes(TableRenderContext context, Sheet sheet) {
        for (int i = 0; i < context.getColumnCount(); i++) {
            try {
                sheet.autoSizeColumn(getColOffset() + i,
                        !mergedRegions.isEmpty());
            } catch (Exception e) {
                // Ignore any problem while calculating size of the columns.
            }
        }
    }

}
