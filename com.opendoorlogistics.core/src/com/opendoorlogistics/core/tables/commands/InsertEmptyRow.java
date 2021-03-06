/*******************************************************************************
 * Copyright (c) 2014 Open Door Logistics (www.opendoorlogistics.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.txt
 ******************************************************************************/
package com.opendoorlogistics.core.tables.commands;

import com.opendoorlogistics.api.tables.ODLDatastore;
import com.opendoorlogistics.api.tables.ODLTable;
import com.opendoorlogistics.api.tables.ODLTableAlterable;
import com.opendoorlogistics.api.tables.ODLTableDefinition;

final public class InsertEmptyRow extends Command{
	final private int insertAt;
	final private long rowId;
	
	public InsertEmptyRow(int tableId, int insertAt, long rowId) {
		super(tableId);
		this.insertAt = insertAt;
		this.rowId = rowId;
	}

	@Override
	public Command doCommand(ODLDatastore<? extends ODLTableDefinition> database) {	
		ODLTable table = (ODLTableAlterable)database.getTableByImmutableId(tableId);
		if(table==null){
			return null;
		}
		
	//	System.out.println("Inserting row " + rowId);
		
		table.insertEmptyRow(insertAt,rowId);
		return new DeleteRow(tableId, insertAt);
	}


}
