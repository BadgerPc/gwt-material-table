/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2017 GwtMaterialDesign
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
package gwt.material.design.client.data;

import com.google.gwt.event.shared.HandlerRegistration;
import gwt.material.design.client.data.events.CategoryClosedHandler;
import gwt.material.design.client.data.events.CategoryOpenedHandler;
import gwt.material.design.client.data.events.ColumnSortHandler;
import gwt.material.design.client.data.events.ComponentsRenderedHandler;
import gwt.material.design.client.data.events.RenderedHandler;
import gwt.material.design.client.data.events.RowCollapsedHandler;
import gwt.material.design.client.data.events.RowCollapsingHandler;
import gwt.material.design.client.data.events.RowContextMenuHandler;
import gwt.material.design.client.data.events.RowDoubleClickHandler;
import gwt.material.design.client.data.events.RowExpandedHandler;
import gwt.material.design.client.data.events.RowExpandingHandler;
import gwt.material.design.client.data.events.RowLongPressHandler;
import gwt.material.design.client.data.events.RowSelectHandler;
import gwt.material.design.client.data.events.RowShortPressHandler;
import gwt.material.design.client.data.events.SelectAllHandler;

import java.util.List;

/**
 * @author Ben Dol
 */
public interface EventHandlers<T> {

    /**
     * Add a handler that is triggered when select all rows is executed.
     */
    HandlerRegistration addSelectAllHandler(SelectAllHandler<T> handler);

    /**
     * Adda a handler that is triggered when a row is selected.
     */
    HandlerRegistration addRowSelectHandler(RowSelectHandler<T> handler);

    /**
     * Add a handler that triggers when a row is expanding.
     */
    HandlerRegistration addRowExpandingHandler(RowExpandingHandler<T> handler);

    /**
     * Add a handler that triggers when a row has expanded.
     */
    HandlerRegistration addRowExpandedHandler(RowExpandedHandler<T> handler);

    /**
     * Add a handler that triggers when a row is collapsing.
     */
    HandlerRegistration addRowCollapseHandler(RowCollapsingHandler<T> handler);

    /**
     * Add a handler that triggers when a row has collapsed.
     */
    HandlerRegistration addRowCollapsedHandler(RowCollapsedHandler<T> handler);

    /**
     * Add a handler that triggers when a row is right clicked.
     */
    HandlerRegistration addRowContextMenuHandler(RowContextMenuHandler<T> handler);

    /**
     * Add a handler that triggers when a row is double clicked.
     */
    HandlerRegistration addRowDoubleClickHandler(RowDoubleClickHandler<T> handler);

    /**
     * Add a handler that triggers when a row is long pressed.
     */
    HandlerRegistration addRowLongPressHandler(RowLongPressHandler<T> handler);

    /**
     * Add a handler that triggers when a row is short pressed.
     */
    HandlerRegistration addRowShortPressHandler(RowShortPressHandler<T> handler);

    /**
     * Add a handler that triggers when a column is sorted.
     */
    HandlerRegistration addColumnSortHandler(ColumnSortHandler<T> handler);

    /**
     * Add a handler that triggers when a category is opened.
     */
    HandlerRegistration addCategoryOpenedHandler(CategoryOpenedHandler handler);

    /**
     * Add a handler that triggers when a category is closed.
     */
    HandlerRegistration addCategoryClosedHandler(CategoryClosedHandler handler);

    /**
     * Add a handler that triggers when all the components have rendered,
     * this can fire multiple times depending on the table settings.<br><br>
     * Also see {@link #addRenderedHandler(RenderedHandler)}.
     */
    HandlerRegistration addComponentsRenderedHandler(ComponentsRenderedHandler handler);

    /**
     * Add a handler that triggers when all the row data has rendered after calling
     * {@link gwt.material.design.client.data.AbstractDataView#setRowData(int, List)}.<br>
     * This will only fire once per call even if the data is re-rendered in the cases of sorting, etc.
     */
    HandlerRegistration addRenderedHandler(RenderedHandler handler);
}
