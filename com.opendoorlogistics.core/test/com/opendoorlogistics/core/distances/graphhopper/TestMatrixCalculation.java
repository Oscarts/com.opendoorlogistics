/*******************************************************************************
 * Copyright (c) 2014 Open Door Logistics (www.opendoorlogistics.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.txt
 ******************************************************************************/
package com.opendoorlogistics.core.distances.graphhopper;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.graphhopper.util.shapes.GHPoint;

public class TestMatrixCalculation {
	private CHMatrixGeneration dijsktra;
	private GHPoint[] points;
	private MatrixResult oneByOne;
	private MatrixResult combined;

	@Before
	public void setUp() throws Exception {
		String graphFolder = "C:\\large_data\\graphhopper\\graphhopper\\europe_great-britain-gh";
		dijsktra = new CHMatrixGeneration(graphFolder);

		int n = 25;
		ExamplePointsData pnts = new ExamplePointsData();
		if (pnts.points.length < n) {
			n = pnts.points.length;
		}

		points = new GHPoint[n];
		for (int i = 0; i < n; i++) {
			points[i] = pnts.points[i];
		}

		System.out.println("Calculating one-by-one");
		oneByOne = dijsktra.calculateMatrixOneByOne(points);
		
		System.out.println("Calculating combined");
		combined = dijsktra.calculateMatrix(points,null);
		
		System.out.println("Starting test");

	}

	@After
	public void tearDown() throws Exception {
		dijsktra.dispose();
	}

	@Test
	public void test() {
		assertEquals(oneByOne.getPointsCount(), combined.getPointsCount());
		assertEquals(points.length, combined.getPointsCount());

		int n = oneByOne.getPointsCount();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double da = oneByOne.getDistanceMetres(i,j);
				double db = combined.getDistanceMetres(i,j);
				assertEquals(da,db, 0.00001 * da);
				
				double ta = oneByOne.getTimeMilliseconds(i,j);
				double tb = combined.getTimeMilliseconds(i,j);
				assertEquals(ta,tb, 0.00001 * ta);
				
			}
		}
	}

}
