/******************************************************************************
 * Project:  NextGIS mobile
 * Purpose:  Mobile GIS for Android.
 * Author:   Dmitry Baryshnikov (aka Bishop), polimax@mail.ru
 ******************************************************************************
 *   Copyright (C) 2014 NextGIS
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/
package com.nextgis.maplib.datasource;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.nextgis.maplib.util.GeoConstants.*;

public class GeoLineString extends GeoGeometry {

    protected List<GeoPoint> mPoints;

    public GeoLineString() {
        mPoints = new ArrayList<GeoPoint>();
    }

    public List<GeoPoint> getPoints() {
        return mPoints;
    }

    public void add(GeoPoint point) throws IllegalArgumentException {
        if (point == null) {
            throw new IllegalArgumentException("GeoLineString: point == null.");
        }

        mPoints.add(point);
    }

    public GeoPoint remove(int index) {
        return mPoints.remove(index);
    }

    @Override
    public int getType() {
        return GTLineString;
    }

    @Override
    protected boolean rawProject(int toCrs) {
        boolean isOk = true;
        for (GeoPoint point : mPoints) {
            isOk = isOk && point.rawProject(toCrs);
        }
        return isOk;
    }

    @Override
    public GeoEnvelope getEnvelope() {
        GeoEnvelope envelope = new GeoEnvelope();

        for (GeoPoint point : mPoints) {
            envelope.merge(point.getEnvelope());
        }

        return envelope;
    }

    @Override
    public void setCoordinatesFromJSON(JSONArray coordinates) throws JSONException {
        if (coordinates.length() < 2) {
            throw new JSONException("For type \"LineString\", the \"coordinates\" member must be an array of two or more positions.");
        }

        for (int i = 0; i < coordinates.length(); ++i) {
            GeoPoint point = new GeoPoint();
            point.setCoordinatesFromJSON(coordinates.getJSONArray(i));
            add(point);
        }
    }

    @Override
    public JSONArray coordinatesToJSON() throws JSONException {
        JSONArray coordinates = new JSONArray();

        for (GeoPoint point : this.mPoints) {
            coordinates.put(point.coordinatesToJSON());
        }

        return coordinates;
    }
}