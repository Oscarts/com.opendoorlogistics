/*******************************************************************************
 * Copyright (c) 2014 Open Door Logistics (www.opendoorlogistics.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.txt
 ******************************************************************************/
package com.opendoorlogistics.core.api.impl;

import com.opendoorlogistics.api.StringConventions;
import com.opendoorlogistics.api.ODLApi;
import com.opendoorlogistics.api.StandardComponents;
import com.opendoorlogistics.api.Tables;
import com.opendoorlogistics.api.Values;
import com.opendoorlogistics.api.components.ODLComponentProvider;
import com.opendoorlogistics.api.geometry.Geometry;
import com.opendoorlogistics.api.standardcomponents.Maps;
import com.opendoorlogistics.api.ui.UIFactory;
import com.opendoorlogistics.core.components.ODLGlobalComponents;

public class ODLApiImpl implements ODLApi{
	private StringConventions conventions;
	private StandardComponents standardComponents;
	private Values conversionApi;
	private Tables tables;
	private Geometry geometry;
	private UIFactory uiFactory;


	@Override
	public Values values() {
		if(conversionApi==null){
			conversionApi = new ValuesImpl();
		}
		return conversionApi;
	}

	@Override
	public Tables tables() {
		if(tables==null){
			tables = new TablesImpl();			
		}
		return tables;
	}

	@Override
	public StringConventions stringConventions() {
		if(conventions==null){
			conventions = new StringConventionsImpl();
		}
		return conventions;
	}

	@Override
	public Geometry geometry() {
		if(geometry==null){
			geometry = new GeometryImpl();
		}
		return geometry;
	}

	@Override
	public UIFactory uiFactory() {
		if(uiFactory==null){
			uiFactory = new UIFactoryImpl();
		}
		return uiFactory;
	}

	@Override
	public StandardComponents standardComponents() {
		if(standardComponents==null){
			standardComponents = new StandardComponentsImpl();
		}
		return standardComponents;
	}

	@Override
	public ODLComponentProvider registeredComponents() {
		return ODLGlobalComponents.getProvider();
	}

	
}
