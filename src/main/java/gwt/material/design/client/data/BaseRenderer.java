package gwt.material.design.client.data;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import gwt.material.design.client.base.constants.StyleName;
import gwt.material.design.client.base.constants.TableCssName;
import gwt.material.design.client.constants.HideOn;
import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.constants.WavesType;
import gwt.material.design.client.data.component.CategoryComponent;
import gwt.material.design.client.data.component.Component;
import gwt.material.design.client.data.component.RowComponent;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.html.Div;
import gwt.material.design.client.ui.table.TableData;
import gwt.material.design.client.ui.table.TableHeader;
import gwt.material.design.client.ui.table.TableRow;
import gwt.material.design.client.ui.table.TableSubHeader;
import gwt.material.design.client.ui.table.cell.Column;
import gwt.material.design.client.ui.table.cell.FrozenProperties;
import gwt.material.design.client.ui.table.cell.FrozenSide;
import gwt.material.design.client.ui.table.cell.WidgetColumn;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Base Component Renderer used by {@link AbstractDataView}.
 * <br><br>
 * This can be extended upon or can be replaced all together.
 *
 * @author Ben Dol
 */
public class BaseRenderer<T> implements Renderer<T> {

    private final Logger logger = Logger.getLogger(BaseRenderer.class.getName());

    // Configurations
    private int calculatedRowHeight = 55;
    private int expectedRowHeight = calculatedRowHeight;

    private IconType sortAscIcon = IconType.ARROW_UPWARD;
    private IconType sortDescIcon = IconType.ARROW_DOWNWARD;
    private IconSize sortIconSize = IconSize.TINY;

    @Override
    public TableRow drawRow(DataView<T> dataView, RowComponent<T> rowComponent, Object valueKey,
                            List<Column<T, ?>> columns, boolean redraw) {
        T data = rowComponent.getData();
        TableRow row = rowComponent.getWidget();
        List<TableHeader> headers = dataView.getHeaders();
        boolean draw = true;
        if(row == null) {
            // Create a new row element
            row = new TableRow();
            row.getElement().getStyle().setDisplay(Display.NONE);
            row.getElement().getStyle().setProperty("height", getExpectedRowHeight() + "px");
            row.getElement().getStyle().setProperty("maxHeight", getExpectedRowHeight() + "px");
            row.getElement().getStyle().setProperty("minHeight", getExpectedRowHeight() + "px");
            row.setStyleName(TableCssName.DATA_ROW);
            rowComponent.setWidget(row);

            if(!dataView.getSelectionType().equals(SelectionType.NONE)) {
                TableData selection = drawSelectionCell();
                if(rowComponent.hasLeftFrozen()) {
                    drawColumnFreeze(selection, rowComponent, headers.get(0), null, FrozenSide.LEFT);
                }
                row.add(selection);
            }
        } else if(!redraw && !rowComponent.isRedraw()) {
            draw = false;
        }

        if(draw) {
            // Build the columns
            int colOffset = dataView.getColumnOffset();
            int colSize = columns.size();

            for(int c = 0; c < colSize; c++) {
                int colIndex = c + colOffset;
                Context context = new Context(rowComponent.getIndex(), colIndex, valueKey);
                Column<T, ?> column = columns.get(c);
                TableData td = drawColumn(row, context, data, column, colIndex, dataView.isHeaderVisible(colIndex));
                drawColumnFreeze(td, rowComponent, headers.get(colIndex), column, column.getFrozenSide());
            }
            rowComponent.setRedraw(false);
        }

        if(dataView.isUseRowExpansion()) {
            if(!row.hasExpansionColumn()) {
                TableData expand = new TableData();
                expand.setId("colex");
                MaterialIcon expandIcon = new MaterialIcon();
                expandIcon.setId("expand");
                expandIcon.setWidth("100%");
                expandIcon.setIconType(IconType.KEYBOARD_ARROW_DOWN);
                expandIcon.setWaves(WavesType.LIGHT);
                expandIcon.getElement().getStyle().setCursor(Cursor.POINTER);
                expand.add(expandIcon);

                if(rowComponent.hasRightFrozen()) {
                    drawColumnFreeze(expand, rowComponent, headers.get(0), null, FrozenSide.RIGHT);
                }
                row.add(expand);
            }
        } else if(row.hasExpansionColumn()) {
            row.removeExpansionColumn();
        }

        Scheduler.get().scheduleDeferred(() -> {
            calculateRowHeight(rowComponent);
        });
        return row;
    }

    @Override
    public TableSubHeader drawCategory(CategoryComponent category) {
        if(category != null) {
            TableSubHeader subHeader = category.getWidget();
            if(subHeader == null) {
                subHeader = category.render();
            }
            return subHeader;
        }

        // No subheader was added
        return null;
    }

    @Override
    public TableRow drawCustom(Component<?> component) {
        return new TableRow();
    }

    @Override
    public TableData drawSelectionCell() {
        TableData checkBox = new TableData();
        checkBox.setId("col0");
        checkBox.addStyleName(TableCssName.SELECTION);
        new MaterialCheckBox(checkBox.getElement());
        return checkBox;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableData drawColumn(TableRow row, Context context, T rowValue, Column<T, ?> column, int beforeIndex, boolean visible) {
        TableData data = null;
        if(row != null && rowValue != null) {
            data = row.getColumn(beforeIndex);
            if(data == null) {
                data = new TableData();
                row.insert(data, beforeIndex);
            } else {
                data.clear();
            }

            Div wrapper = new Div();

            // Render the column cell
            if(column instanceof WidgetColumn) {
                wrapper.setStyleName(TableCssName.WIDGET_CELL);
                wrapper.add(((WidgetColumn) column).render(context, rowValue));
            } else {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                column.render(context, rowValue, sb);
                wrapper.getElement().setInnerHTML(sb.toSafeHtml().asString());
                wrapper.setStyleName(TableCssName.CELL);
            }

            data.add(wrapper);

            data.setId("col" + beforeIndex);
            data.setDataTitle(column.getName());
            HideOn hideOn = column.getHideOn();
            if(hideOn != null) {
                data.setHideOn(hideOn);
            }
            TextAlign textAlign = column.getTextAlign();
            if(textAlign != null) {
                data.setTextAlign(textAlign);
            }
            if(column.isNumeric()) {
                data.addStyleName(TableCssName.NUMERIC);
            }

            // Apply the style properties
            Style style = data.getElement().getStyle();
            Map<StyleName, String> styleProps = column.getStyleProperties();
            if(styleProps != null) {
                styleProps.forEach((s, v) -> style.setProperty(s.styleName(), v));
            }

            // Hide if defined as not visible
            // This can be the case when a header is toggled off.
            if(!visible) {
                data.$this().hide();
            }
        }
        return data;
    }

    @Override
    public TableHeader drawColumnHeader(Column<T, ?> column, String header, int index) {
        MaterialIcon sortIcon = new MaterialIcon();
        sortIcon.setIconSize(sortIconSize);

        TableHeader th = new TableHeader(sortIcon);
        th.setId("col" + index);
        th.setHeader(header);
        HideOn hideOn = column.getHideOn();
        if(hideOn != null) {
            th.setHideOn(hideOn);
        }
        TextAlign textAlign = column.getTextAlign();
        if(textAlign != null) {
            th.setTextAlign(textAlign);
        }
        if(column.isNumeric()) {
            th.addStyleName(TableCssName.NUMERIC);
        }

        // Apply the style properties
        Style style = th.getElement().getStyle();
        Map<StyleName, String> styleProps = column.getStyleProperties();
        if(styleProps != null) {
            styleProps.forEach((s, v) -> style.setProperty(s.styleName(), v));
        }

        // Set the headers width
        String width = column.getWidth();
        if(width != null) {
            th.setWidth(width);
        }
        th.setVisible(true);
        return th;
    }

    @Override
    public void drawSortIcon(TableHeader th, SortContext<T> sortContext) {
        if(sortContext.getSortDir().equals(SortDir.ASC)) {
            th.getSortIcon().setIconType(sortAscIcon);
        } else {
            th.getSortIcon().setIconType(sortDescIcon);
        }
    }

    @Override
    public void drawColumnFreeze(TableData td, RowComponent<T> rowComponent, TableHeader header, Column<T, ?> column, FrozenSide side) {
        if(column == null || column.isFrozenColumn()) {
            rowComponent.getWidget().$this().hover((e, param1) -> {
                td.$this().addClass("hover");
                return true;
            }, (e, param1) -> {
                td.$this().removeClass("hover");
                return true;
            });

            td.addAttachHandler(event -> {
                Scheduler.get().scheduleDeferred(() -> {
                    int left = header.$this().prevAll().outerWidth();
                    int right = header.$this().nextAll().outerWidth();

                    double width = header.$this().width();
                    double height = rowComponent.getWidget().$this().outerHeight();

                    String paddingTop = td.$this().css("padding-top");
                    String paddingBottom = td.$this().css("padding-bottom");
                    String paddingLeft= td.$this().css("padding-left");
                    String paddingRight = td.$this().css("padding-right");

                    String borderBottom = rowComponent.getWidget().$this().css("border-bottom");

                    td.addStyleName(TableCssName.FROZEN_COL);
                    header.addStyleName(TableCssName.FROZEN_COL);

                    td.$this().width(width + "px");
                    header.$this().width(width + "px");
                    td.$this().height(height + "px");
                    header.$this().height(height + "px");

                    td.$this().css("border-bottom", borderBottom);
                    header.$this().css("border-bottom", borderBottom);

                    td.$this().css("padding-top", paddingTop);
                    header.$this().css("padding-top", paddingTop);

                    td.$this().css("padding-bottom", paddingBottom);
                    header.$this().css("padding-bottom", paddingBottom);

                    td.$this().css("padding-left", paddingLeft);
                    header.$this().css("padding-left", paddingLeft);

                    td.$this().css("padding-right", paddingRight);
                    header.$this().css("padding-right", paddingRight);

                    if(column != null) {
                        // Apply the style properties
                        FrozenProperties frozenProps = column.getFrozenProperties();
                        if(frozenProps != null) {
                            Style styleTd = td.getElement().getStyle();
                            frozenProps.forEach((s, v) -> styleTd.setProperty(s.styleName(), v));

                            Style styleHeader = header.getElement().getStyle();
                            frozenProps.getHeaderStyleProperties().forEach((s, v) -> styleHeader.setProperty(s.styleName(), v));
                        }
                    }

                    if((column != null && column.isFrozenLeft()) || side.equals(FrozenSide.LEFT)) {
                        // Left freeze
                        td.setLeft(left);
                        header.setLeft(left);
                    } else if((column != null && column.isFrozenRight()) || side.equals(FrozenSide.RIGHT)) {
                        // Right freeze
                        td.setRight(right);
                        td.$this().css("left", "auto");
                        header.setRight(right);
                        header.$this().css("left", "auto");
                    }
                });
            }, true);
        }
    }

    @Override
    public int getExpectedRowHeight() {
        return expectedRowHeight;
    }

    @Override
    public void setExpectedRowHeight(int expectedRowHeight) {
        if(expectedRowHeight < 33) {
            logger.warning("Expected row height must be 33px or higher, setting row height to 33px.");
            this.expectedRowHeight = 33;
        } else {
            this.expectedRowHeight = expectedRowHeight;
        }
    }

    @Override
    public void calculateRowHeight(RowComponent<T> row) {
        TableRow element = row.getWidget();
        if(element != null) {
            int rowHeight = element.$this().outerHeight(true);
            if (rowHeight > 0 && rowHeight != calculatedRowHeight) {
                calculatedRowHeight = rowHeight;
            }
        }
    }

    @Override
    public int getCalculatedRowHeight() {
        return calculatedRowHeight;
    }
}
