/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.tasktracker.server.beans;

import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.table.model.SearchResultsTableModel;
import com.bc.appcore.util.Selection;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.WebApp;
import com.bc.tasktracker.jpa.entities.master.Task;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.swing.table.TableModel;
import com.bc.appcore.jpa.model.EntityResultModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 7, 2017 2:48:14 PM
 */
public class TableModelBean implements Serializable {

    private EntityResultModel resultModel;
    private TableModel tableModel;
    
    private int rowIndex;
    
    private int columnIndex;
    
    private WebApp app;
    
    private SelectionContext sc;
    
    private final Class entityType;
    
    public TableModelBean() { 
        this(Task.class);
    }
    
    public TableModelBean(Class entityType) { 
        this.entityType = Objects.requireNonNull(entityType);
    }
    
    public void setRequest(HttpServletRequest request) {
        app = (WebApp)request.getServletContext().getAttribute(AttributeNames.APP);
        tableModel = (TableModel)request.getSession().getAttribute(AttributeNames.SEARCH_RESULTS_PAGE_TABLE_MODEL);
        if(tableModel instanceof SearchResultsTableModel) {
            resultModel = ((SearchResultsTableModel)tableModel).getResultModel();
        }
    }
    
    public boolean isIdColumn() {
        final String columnName = this.getColumnName();
        return columnName.equals(app.getActivePersistenceUnitContext().getMetaData().getIdColumnName(entityType));
    }
    
    public boolean isBooleanType() {
        final Class columnClass = this.getColumnClass();
        return java.lang.Boolean.class.isAssignableFrom(columnClass);
    }

    public boolean isNumberType() {
        final Class columnClass = this.getColumnClass();
        return java.lang.Number.class.isAssignableFrom(columnClass);
    }
    
    public boolean isStringType() {
        final Class columnClass = this.getColumnClass();
        return java.lang.String.class.isAssignableFrom(columnClass);
    }
    
    public boolean isDateType() {
        final Class columnClass = this.getColumnClass();
        return java.util.Date.class.isAssignableFrom(columnClass);
    }
    
    public boolean isSelectionType() {
        final Class columnClass = this.getColumnClass();
        return this.getSelectionContext().isSelectionType(columnClass);
    }
    
    public SelectionContext getSelectionContext() {
        if(sc == null) {
            sc = app.getOrException(SelectionContext.class);
        }
        return sc;
    }
    
    public List<Selection> getSelectionValues() {
        final Class columnClass = this.getColumnClass();
        return this.getSelectionContext().getSelectionValues(columnClass);
    }
    
    public int getRowIndex() {
        return rowIndex;
    }
    
    public void setColumnName(String columnName) {
        final int colCount = this.getColumnCount();
        for(int col=0; col<colCount; col++) {
            if(columnName.equals(this.getTableModel().getColumnName(col))) {
                this.columnIndex = col;
                break;
            }
        }
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    public int getRowCount() {
        return tableModel.getRowCount();
    }

    public int getColumnCount() {
        return tableModel.getColumnCount();
    }

    public String getColumnName() {
        final String columnName;
        if(resultModel != null) {
            columnName = resultModel.getColumnName(columnIndex);
        }else{
            columnName = tableModel.getColumnName(columnIndex);
        }
        return columnName;
    }

    public String getColumnLabel() {
        final String columnName;
        if(resultModel != null) {
            columnName = resultModel.getColumnLabel(columnIndex);
        }else{
            columnName = tableModel.getColumnName(columnIndex);
        }
        return columnName;
    }
    
    public Class<?> getColumnClass() {
        return tableModel.getColumnClass(columnIndex);
    }

    public boolean isCellEditable() {
        return tableModel.isCellEditable(rowIndex, columnIndex);
    }

    public Object getValueAt() {
        return tableModel.getValueAt(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue) {
        tableModel.setValueAt(aValue, rowIndex, columnIndex);
    }
}
