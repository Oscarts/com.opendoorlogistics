/*******************************************************************************
 * Copyright (c) 2014 Open Door Logistics (www.opendoorlogistics.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.txt
 ******************************************************************************/
package com.opendoorlogistics.core.tables.io;

import java.io.File;
import java.io.InputStream;

import com.opendoorlogistics.api.ExecutionReport;
import com.opendoorlogistics.api.components.ProcessingApi;
import com.opendoorlogistics.api.tables.ODLDatastoreAlterable;
import com.opendoorlogistics.api.tables.ODLTableAlterable;
import com.opendoorlogistics.core.geometry.ImportShapefile;
import com.opendoorlogistics.core.tables.ODLFactory;
import com.opendoorlogistics.core.utils.io.TextIO;

final public class TableIOUtils {
	public static ODLDatastoreAlterable<ODLTableAlterable> importFile(File file, SupportedFileType type,ProcessingApi processingApi, ExecutionReport report){
		switch(type){
		case CSV:
			return TextIO.importCSV(file);
			
		case TAB:
			return TextIO.importTabbed(file);
			
		case EXCEL:
			return PoiIO.importExcel(file, processingApi,report);
			
		case SHAPEFILE_LINKED_GEOM:
			return ImportShapefile.importShapefile(file,true);
			
		case SHAPEFILE_COPIED_GEOM:
			return ImportShapefile.importShapefile(file,false);
		}
		return null;
	}
	
	public static ODLDatastoreAlterable<ODLTableAlterable> importExampleDatastore(String name, ExecutionReport report){
		ODLDatastoreAlterable<ODLTableAlterable> ret =null;
		// Use own class loader to prevent problems when jar loaded by reflection
		InputStream is = TableIOUtils.class.getResourceAsStream( "/resources/datastores/"+ name);	
		try {
			ret = PoiIO.importExcel(is, report);
		} catch (Exception e) {
			ret = null;
			if(report!=null){
				report.setFailed(e);				
			}
		}
		finally{
			if(is!=null){
				try {
					is.close();					
				} catch (Exception e2) {
					if(report!=null){
						report.setFailed(e2);				
					}
				}
			}
		}
		return ret;
	}
}
