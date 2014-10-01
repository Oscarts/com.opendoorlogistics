/*******************************************************************************
 * Copyright (c) 2014 Open Door Logistics (www.opendoorlogistics.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.txt
 ******************************************************************************/
package com.opendoorlogistics.core.gis.map.transforms;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.opendoorlogistics.api.geometry.LatLong;

public interface LatLongToScreen {
	Point2D getOnScreenPixelPosition(LatLong latLong);
	Point2D getWorldBitmapPixelPosition(LatLong latLong);
	Rectangle2D getViewportWorldBitmapScreenPosition();
	LatLong getLongLat(int pixelX, int pixelY);
	
	/**
	 * A hash-map compatible key which identifies the zoom level.
	 * Subsequent valls to the renderer where call 1 has zoomKey1
	 * and call 3 has zoomKey2 will have the same zoom level (per pixel
	 * resolution etc) is zoomKey1.equals(zoomKey2).
	 * @return
	 */
	Object getZoomHashmapKey();
}
