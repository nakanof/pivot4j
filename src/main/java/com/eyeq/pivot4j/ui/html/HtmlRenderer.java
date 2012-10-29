/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 */
package com.eyeq.pivot4j.ui.html;

import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.eyeq.pivot4j.ui.AbstractMarkupRenderer;
import com.eyeq.pivot4j.ui.RenderContext;
import com.eyeq.pivot4j.ui.command.CellCommand;

public class HtmlRenderer extends AbstractMarkupRenderer {

	private String tableId;

	private String tableStyleClass = "pv-table";

	private Integer border;

	private Integer cellSpacing;

	private Integer cellPadding;

	private String rowStyleClass = "pv-row";

	private String evenRowStyleClass = "pv-row-even";

	private String oddRowStyleClass = "pv-row-odd";

	private String columnHeaderStyleClass = "pv-col-hdr";

	private String rowHeaderStyleClass = "pv-row-hdr";

	private String columnTitleStyleClass = columnHeaderStyleClass;

	private String rowTitleStyleClass = rowHeaderStyleClass;

	private String cellStyleClass = "pv-cell";

	private String cornerStyleClass = "pv-corner";

	private int rowHeaderLevelPadding = 10;

	private Set<CellCommand> commands;

	/**
	 * @param writer
	 */
	public HtmlRenderer(Writer writer) {
		super(writer);
	}

	/**
	 * @return the tableId
	 */
	public String getTableId() {
		return tableId;
	}

	/**
	 * @param tableId
	 *            the tableId to set
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	/**
	 * @return the tableStyleClass
	 */
	public String getTableStyleClass() {
		return tableStyleClass;
	}

	/**
	 * @param tableStyleClass
	 *            the tableStyleClass to set
	 */
	public void setTableStyleClass(String tableStyleClass) {
		this.tableStyleClass = tableStyleClass;
	}

	/**
	 * @return the cellSpacing
	 */
	public Integer getCellSpacing() {
		return cellSpacing;
	}

	/**
	 * @param cellSpacing
	 *            the cellSpacing to set
	 */
	public void setCellSpacing(Integer cellSpacing) {
		this.cellSpacing = cellSpacing;
	}

	/**
	 * @return the cellPadding
	 */
	public Integer getCellPadding() {
		return cellPadding;
	}

	/**
	 * @param cellPadding
	 *            the cellPadding to set
	 */
	public void setCellPadding(Integer cellPadding) {
		this.cellPadding = cellPadding;
	}

	/**
	 * @return the border
	 */
	public Integer getBorder() {
		return border;
	}

	/**
	 * @param border
	 *            the border to set
	 */
	public void setBorder(Integer border) {
		this.border = border;
	}

	/**
	 * @return the rowStyleClass
	 */
	public String getRowStyleClass() {
		return rowStyleClass;
	}

	/**
	 * @param rowStyleClass
	 *            the rowStyleClass to set
	 */
	public void setRowStyleClass(String rowStyleClass) {
		this.rowStyleClass = rowStyleClass;
	}

	/**
	 * @return the evenRowStyleClass
	 */
	public String getEvenRowStyleClass() {
		return evenRowStyleClass;
	}

	/**
	 * @param evenRowStyleClass
	 *            the evenRowStyleClass to set
	 */
	public void setEvenRowStyleClass(String evenRowStyleClass) {
		this.evenRowStyleClass = evenRowStyleClass;
	}

	/**
	 * @return the oddRowStyleClass
	 */
	public String getOddRowStyleClass() {
		return oddRowStyleClass;
	}

	/**
	 * @param oddRowStyleClass
	 *            the oddRowStyleClass to set
	 */
	public void setOddRowStyleClass(String oddRowStyleClass) {
		this.oddRowStyleClass = oddRowStyleClass;
	}

	/**
	 * @return the columnHeaderStyleClass
	 */
	public String getColumnHeaderStyleClass() {
		return columnHeaderStyleClass;
	}

	/**
	 * @param columnHeaderStyleClass
	 *            the columnHeaderStyleClass to set
	 */
	public void setColumnHeaderStyleClass(String columnHeaderStyleClass) {
		this.columnHeaderStyleClass = columnHeaderStyleClass;
	}

	/**
	 * @return the rowHeaderStyleClass
	 */
	public String getRowHeaderStyleClass() {
		return rowHeaderStyleClass;
	}

	/**
	 * @param rowHeaderStyleClass
	 *            the rowHeaderStyleClass to set
	 */
	public void setRowHeaderStyleClass(String rowHeaderStyleClass) {
		this.rowHeaderStyleClass = rowHeaderStyleClass;
	}

	/**
	 * @return the columnTitleStyleClass
	 */
	public String getColumnTitleStyleClass() {
		return columnTitleStyleClass;
	}

	/**
	 * @param columnTitleStyleClass
	 *            the columnTitleStyleClass to set
	 */
	public void setColumnTitleStyleClass(String columnTitleStyleClass) {
		this.columnTitleStyleClass = columnTitleStyleClass;
	}

	/**
	 * @return the rowTitleStyleClass
	 */
	public String getRowTitleStyleClass() {
		return rowTitleStyleClass;
	}

	/**
	 * @param rowTitleStyleClass
	 *            the rowTitleStyleClass to set
	 */
	public void setRowTitleStyleClass(String rowTitleStyleClass) {
		this.rowTitleStyleClass = rowTitleStyleClass;
	}

	/**
	 * @return the cellStyleClass
	 */
	public String getCellStyleClass() {
		return cellStyleClass;
	}

	/**
	 * @param cellStyleClass
	 *            the cellStyleClass to set
	 */
	public void setCellStyleClass(String cellStyleClass) {
		this.cellStyleClass = cellStyleClass;
	}

	/**
	 * @return the cornerStyleClass
	 */
	public String getCornerStyleClass() {
		return cornerStyleClass;
	}

	/**
	 * @param cornerStyleClass
	 *            the cornerStyleClass to set
	 */
	public void setCornerStyleClass(String cornerStyleClass) {
		this.cornerStyleClass = cornerStyleClass;
	}

	/**
	 * @return the rowHeaderLevelPadding
	 */
	public int getRowHeaderLevelPadding() {
		return rowHeaderLevelPadding;
	}

	/**
	 * @param rowHeaderLevelPadding
	 *            the rowHeaderLevelPadding to set
	 */
	public void setRowHeaderLevelPadding(int rowHeaderLevelPadding) {
		this.rowHeaderLevelPadding = rowHeaderLevelPadding;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#startTable(com.eyeq.pivot4j.ui
	 *      .RenderContext)
	 */
	@Override
	public void startTable(RenderContext context) {
		startElement(context, "table", getTableAttributes(context));
	}

	/**
	 * @param context
	 * @return
	 */
	protected Map<String, String> getTableAttributes(RenderContext context) {
		Map<String, String> attributes = new TreeMap<String, String>();

		if (tableId != null) {
			attributes.put("id", tableId);
		}

		if (tableStyleClass != null) {
			attributes.put("class", tableStyleClass);
		}

		if (cellSpacing != null) {
			attributes.put("cellspacing", Integer.toString(cellSpacing));
		}

		if (cellPadding != null) {
			attributes.put("cellpadding", Integer.toString(cellPadding));
		}

		if (border != null) {
			attributes.put("border", Integer.toString(border));
		}

		return attributes;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#startHeader(com.eyeq.pivot4j.
	 *      ui.RenderContext)
	 */
	@Override
	public void startHeader(RenderContext context) {
		startElement(context, "thead", getHeaderAttributes(context));
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#endHeader(com.eyeq.pivot4j.ui
	 *      .RenderContext)
	 */
	@Override
	public void endHeader(RenderContext context) {
		endElement(context, "thead");
	}

	/**
	 * @param context
	 * @return
	 */
	protected Map<String, String> getHeaderAttributes(RenderContext context) {
		return Collections.emptyMap();
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#startBody(com.eyeq.pivot4j.ui
	 *      .RenderContext)
	 */
	@Override
	public void startBody(RenderContext context) {
		startElement(context, "tbody", getBodyAttributes(context));
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#endBody(com.eyeq.pivot4j.ui.
	 *      RenderContext)
	 */
	@Override
	public void endBody(RenderContext context) {
		endElement(context, "tbody");
	}

	/**
	 * @param context
	 * @return
	 */
	protected Map<String, String> getBodyAttributes(RenderContext context) {
		return Collections.emptyMap();
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#endTable(com.eyeq.pivot4j.ui.
	 *      RenderContext)
	 */
	@Override
	public void endTable(RenderContext context) {
		endElement(context, "table");
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#startRow(com.eyeq.pivot4j.ui.
	 *      RenderContext)
	 */
	@Override
	public void startRow(RenderContext context) {
		startElement(context, "tr", getRowAttributes(context));
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#endRow(com.eyeq.pivot4j.ui.
	 *      RenderContext)
	 */
	@Override
	public void endRow(RenderContext context) {
		endElement(context, "tr");
	}

	/**
	 * @param context
	 * @return
	 */
	protected Map<String, String> getRowAttributes(RenderContext context) {
		Map<String, String> attributes = new TreeMap<String, String>();

		int index = context.getRowIndex() - context.getColumnHeaderCount();
		if (index < 0) {
			index = context.getRowIndex();
		}

		if (rowStyleClass != null || evenRowStyleClass != null
				|| oddRowStyleClass != null) {
			boolean first = true;

			StringBuilder builder = new StringBuilder();

			if (rowStyleClass != null) {
				builder.append(rowStyleClass);
				first = false;
			}

			boolean even = index % 2 == 0;
			if (even && evenRowStyleClass != null) {
				if (first) {
					first = false;
				} else {
					builder.append(' ');
				}

				builder.append(evenRowStyleClass);
			} else if (!even && oddRowStyleClass != null) {
				if (first) {
					first = false;
				} else {
					builder.append(' ');
				}

				builder.append(oddRowStyleClass);
			}

			attributes.put("class", builder.toString());
		}

		return attributes;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.AbstractPivotRenderer#startCell(com.eyeq.pivot4j.ui.RenderContext,
	 *      java.util.Set)
	 */
	@Override
	public void startCell(RenderContext context, Set<CellCommand> commands) {
		boolean header;

		switch (context.getCellType()) {
		case ColumnHeader:
		case RowHeader:
		case ColumnTitle:
		case RowTitle:
		case None:
			header = true;
			break;
		default:
			header = false;
			break;
		}

		String name = header ? "th" : "td";

		startElement(context, name, getCellAttributes(context));

		this.commands = commands;

		if (commands != null && !commands.isEmpty()) {
			startCommand(context, commands);
		}
	}

	/**
	 * @see com.eyeq.pivot4j.ui.PivotRenderer#endCell(com.eyeq.pivot4j.ui.
	 *      RenderContext)
	 */
	@Override
	public void endCell(RenderContext context) {
		if (commands != null && !commands.isEmpty()) {
			endCommand(context, commands);
			this.commands = null;
		}

		switch (context.getCellType()) {
		case ColumnHeader:
		case RowHeader:
		case ColumnTitle:
		case RowTitle:
		case None:
			endElement(context, "th");
			break;
		default:
			endElement(context, "td");
			break;
		}
	}

	/**
	 * @param context
	 * @return
	 */
	protected Map<String, String> getCellAttributes(RenderContext context) {
		String styleClass = null;
		String style = null;

		switch (context.getCellType()) {
		case ColumnHeader:
			styleClass = columnHeaderStyleClass;
			break;
		case RowHeader:
			styleClass = rowHeaderStyleClass;

			if (rowHeaderLevelPadding > 0) {
				int padding = rowHeaderLevelPadding
						* (1 + context.getMember().getDepth());
				style = "padding-left: " + padding + "px;";
			}

			break;
		case ColumnTitle:
			styleClass = columnTitleStyleClass;
			break;
		case RowTitle:
			styleClass = rowTitleStyleClass;
			break;
		case Value:
			styleClass = cellStyleClass;
			break;
		case None:
			styleClass = cornerStyleClass;
			break;
		default:
			assert false;
		}

		Map<String, String> attributes = new TreeMap<String, String>();

		if (styleClass != null) {
			attributes.put("class", styleClass);
		}

		if (style != null) {
			attributes.put("style", style);
		}

		if (context.getColSpan() > 1) {
			attributes.put("colspan", Integer.toString(context.getColSpan()));
		}

		if (context.getRowSpan() > 1) {
			attributes.put("rowspan", Integer.toString(context.getRowSpan()));
		}

		return attributes;
	}

	/**
	 * @param context
	 * @param commands
	 */
	public void startCommand(RenderContext context, Set<CellCommand> commands) {
	}

	/**
	 * @param context
	 * @param commands
	 */
	public void endCommand(RenderContext context, Set<CellCommand> commands) {
	}

	/**
	 * @see com.eyeq.pivot4j.ui.AbstractPivotRenderer#getCellLabel(com.eyeq.pivot4j.ui.RenderContext)
	 */
	@Override
	protected String getCellLabel(RenderContext context) {
		return StringUtils
				.defaultIfEmpty(super.getCellLabel(context), "&nbsp;");
	}
}