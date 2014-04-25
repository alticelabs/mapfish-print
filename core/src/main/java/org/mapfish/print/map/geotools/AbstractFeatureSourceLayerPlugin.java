/*
 * Copyright (C) 2014  Camptocamp
 *
 * This file is part of MapFish Print
 *
 * MapFish Print is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Print is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Print.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mapfish.print.map.geotools;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

import jsr166y.ForkJoinPool;

import org.geotools.data.FeatureSource;
import org.geotools.styling.Style;
import org.mapfish.print.config.Template;
import org.mapfish.print.map.MapLayerFactoryPlugin;
import org.mapfish.print.map.style.StyleParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.util.Set;

import javax.annotation.Nullable;

/**
 * Abstract class for FeatureSource based {@link org.mapfish.print.map.MapLayerFactoryPlugin} objects.
 *
 * @param <P> the type of parameter
 * @author Jesse on 4/9/2014.
 *
 * CSOFF:VisibilityModifier
 */
public abstract class AbstractFeatureSourceLayerPlugin<P> implements MapLayerFactoryPlugin<P> {

    @Autowired
    private StyleParser parser;
    /**
     * A fork join pool for running async tasks.
     */
    @Autowired
    protected ForkJoinPool forkJoinPool;
    /**
     * A http request factory for making http requests.
     */
    @Autowired
    protected ClientHttpRequestFactory httpRequestFactory;

    private final Set<String> typeNames;

    /**
     * Constructor.
     *
     * @param typeName at least one type name for identifying the plugin is required.
     * @param typeNames additional strings used to identify if this plugin can handle the layer definition.
     */
    public AbstractFeatureSourceLayerPlugin(final String typeName, final String... typeNames) {
        this.typeNames = Sets.newHashSet(typeNames);
        this.typeNames.add(typeName);
    }

    @Override
    public final Set<String> getTypeNames() {
        return this.typeNames;
    }

    /**
     * Create a function that will create the style on demand.  This is called later in a separate thread so any blocking calls
     * will not block the parsing of the layer attributes.
     *
     * @param template    the template for this map
     * @param styleString a string that identifies a style.
     */
    protected final Function<FeatureSource, Style> createStyleFunction(final Template template, final String styleString) {
        return new Function<FeatureSource, Style>() {
            @Nullable
            @Override
            public Style apply(@Nullable final FeatureSource featureCollection) {
                if (featureCollection == null) {
                    throw new IllegalArgumentException("Feature source cannot be null");
                }

                String geomType = featureCollection.getSchema().getGeometryDescriptor().getType().getBinding().getSimpleName();
                String styleRef = styleString;

                if (styleRef == null) {
                    styleRef = geomType;
                }
                return template.getStyle(styleRef)
                        .or(AbstractFeatureSourceLayerPlugin.this.parser.loadStyle(template.getConfiguration(), styleRef))
                        .or(template.getConfiguration().getDefaultStyle(geomType));
            }
        };
    }
}