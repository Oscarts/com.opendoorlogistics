/*******************************************************************************
 * Copyright (c) 2014 Open Door Logistics (www.opendoorlogistics.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.txt
 ******************************************************************************/
package com.opendoorlogistics.studio.tables.grid.adapter;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.opendoorlogistics.api.tables.ODLColumnType;
import com.opendoorlogistics.api.tables.ODLDatastore;
import com.opendoorlogistics.api.tables.ODLListener;
import com.opendoorlogistics.api.tables.ODLTable;
import com.opendoorlogistics.api.tables.ODLTableDefinition;
import com.opendoorlogistics.api.tables.ODLTableReadOnly;
import com.opendoorlogistics.api.tables.TableFlags;
import com.opendoorlogistics.core.tables.ColumnValueProcessor;
import com.opendoorlogistics.core.tables.utils.TableUtils;
import com.opendoorlogistics.core.utils.BiHashMap;
import com.opendoorlogistics.core.utils.strings.StandardisedStringSet;
import com.opendoorlogistics.studio.tables.grid.GridEditPermissions;

public final class SwingAdapter implements TableModel {
	private ODLDatastore<? extends ODLTableReadOnly> ds;
	private int tableId;
	private boolean readOnly;
	private final boolean enableListeners;
	private final BiHashMap<TableModelListener, ODLListener> listeners = new BiHashMap<>();
	private FilteredTableController filter;
	private RowStyler rowStyler;
		
	public SwingAdapter(ODLDatastore<? extends ODLTableReadOnly> ds, int tableId, boolean enableListeners, boolean readOnly, RowStyler rowStyler) {
		this.ds = ds;
		this.tableId = tableId;
		this.enableListeners = enableListeners;
		this.filter = new FilteredTableController(ds, tableId);
		this.readOnly = readOnly;
		this.rowStyler = rowStyler;
	}

	public void replaceData(ODLDatastore<? extends ODLTableReadOnly> ds, int tableId, boolean readOnly, RowStyler rowStyler) {
		this.rowStyler = rowStyler;
		if (enableListeners) {
			throw new RuntimeException();
		}

		this.ds = ds;
		this.tableId = tableId;
		this.readOnly = readOnly;

		// replace the filter object but take the old filter's state
		this.filter = new FilteredTableController(ds, tableId, filter.getFilterState());
	}

	@Override
	public int getRowCount() {
		return getTable().getRowCount() + 1;
	}

	@Override
	public int getColumnCount() {
		return getTable().getColumnCount() + 1;
	}

//	private int getSourceColumn(int index){
//		return index;
//	}
	
	@Override
	public String getColumnName(int columnIndex) {

		if (columnIndex == 0) {
			return "";
		}
		columnIndex--;
	
		return getTable().getColumnName(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		if (columnIndex == 0) {
			return String.class;
		}
		columnIndex--;

		if (getTable().getColumnType(columnIndex) == ODLColumnType.COLOUR) {
			return String.class;
		}

		if (getTable().getColumnType(columnIndex) == ODLColumnType.TIME) {
			return String.class;
		}

		return ColumnValueProcessor.getJavaClass(getTable().getColumnType(columnIndex));
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ODLTableReadOnly table = getTable();
		if (rowIndex >= table.getRowCount()) {
			return null;
		}

		if (columnIndex == 0) {
			return Integer.toString(rowIndex + 1);
		}
		columnIndex--;

		// check columns within range
		if (columnIndex >= table.getColumnCount()) {
			return null;
		}

		ODLColumnType ct = table.getColumnType(columnIndex);
		if (ct == ODLColumnType.COLOUR || ct == ODLColumnType.TIME) {
			return TableUtils.getValueAsString(table, rowIndex, columnIndex);
		}

		return table.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		if (enableListeners) {
			Listener listener = new Listener();
			listeners.put(l, listener);
			ds.addListener(listener, tableId);
		}
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		if (enableListeners) {
			ODLListener listener = listeners.getB(l);
			listeners.removeUsingA(l);
			ds.removeListener(listener);
		}
	}

	public ODLTableReadOnly getTable() {
		return filter.getTable();
	}

	public void dispose() {

	}

	private class Listener implements ODLListener {

		@Override
		public void tableChanged(int tableId, int firstRow, int lastRow) {
			// update the filter first of all
			if (filter != null) {
				filter.update();
			}

			// create the event with all rows changed
			final TableModelEvent tme = new TableModelEvent(SwingAdapter.this, -1, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
			// if(lastRow == Integer.MAX_VALUE){
			// tme = new TableModelEvent(SwingAdapterReadOnly.this, -1, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
			//
			// }else{
			// tme = new TableModelEvent(SwingAdapterReadOnly.this, firstRow, lastRow);
			// }

			// fire the listener
			final TableModelListener tml = listeners.getA(this);
			if (SwingUtilities.isEventDispatchThread()) {
				tml.tableChanged(tme);
			} else {
				try {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							tml.tableChanged(tme);
						}
					});
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}

		}

		@Override
		public void datastoreStructureChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public ODLListenerType getType() {
			return ODLListenerType.TABLE_CHANGED;
		}

	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (readOnly) {
			return false;
		}

		if (columnIndex == 0) {
			return false;
		}

		int tableCol = columnIndex;

		tableCol--;

		ODLTableDefinition table = getTable();
		if (table == null || table.getColumnType(tableCol) == ODLColumnType.IMAGE) {
			return false;
		}

		if ((table.getColumnFlags(tableCol) & TableFlags.FLAG_IS_READ_ONLY) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (readOnly) {
			return;
		}

		ODLTable table = (ODLTable) getTable();

		columnIndex--;

		if (columnIndex < 0) {
			return;
		}

		// check columns within range
		if (columnIndex >= table.getColumnCount()) {
			return;
		}

		boolean startedTransaction = false;
		if (!ds.isInTransaction()) {
			startedTransaction = true;
			ds.startTransaction();
		}
		
		try {
			// grow if its not an empty or null string
			if (aValue != null && aValue.toString().length() > 0) {
				while (rowIndex >= table.getRowCount()) {
					table.createEmptyRow(-1);
				}
			}

			if (rowIndex < table.getRowCount()) {
				if (!ds.isInTransaction()) {
					startedTransaction = true;
					ds.startTransaction();
				}
				
				table.setValueAt(aValue, rowIndex, columnIndex);				
			}
			
		} 
		finally{
			if (startedTransaction) {
				ds.endTransaction();
			}	
		}

	}

	public void setColumnFilter(String column, String value) {
		filter.setColumnFilter(column, value);
	}

	public String getColumnFilter(String column) {
		return filter.getFilterState().getColumnFilter(column);
	}
	
	public String getTableName(){
		return getTable()!=null?getTable().getName() : "";
	}
	
	public StandardisedStringSet getUniqueUnfilteredColumnValues(int col){
		// get the unfiltered table
		StandardisedStringSet ret = new StandardisedStringSet();
		ODLTableReadOnly unfiltered = ds.getTableByImmutableId(tableId);
		if(col>0 && unfiltered!=null){
			col--;
			int n = unfiltered.getRowCount();
			for(int i =0 ; i<n;i++){
				String s = TableUtils.getValueAsString(unfiltered, i, col);
				if(s==null){
					s="";
				}
				ret.add(s);
			}
		}
		return ret;
	}
	
	public int getTableId(){
		return tableId;
	}
	
	public GridEditPermissions getPermissions(){
		GridEditPermissions ret = GridEditPermissions.create(getTable(), filter.isFiltered()==false);
		return ret;
	}
	
	public Color getRowColour(int row){
		if(rowStyler!=null){
			ODLTableReadOnly table = getTable();
			if(table!=null){
				long id = table.getRowId(row);
				if(id!=-1){
					return rowStyler.getRowFontColour(id);
				}
			}
		}
		return null;
	}
}
