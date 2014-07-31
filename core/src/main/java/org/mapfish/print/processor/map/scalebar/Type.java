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

package org.mapfish.print.processor.map.scalebar;

/**
 * Scalebar type.
 */
public enum Type {
    /**
     * A simple line with ticks.
     */
    LINE("line"),

    /**
     * A bar with alternating black and white zones marking the sub-intervals.
     */
    BAR("bar"),

    /**
     * A bar with alternating black and white zones marking the sub-intervals.
     * Intervals have small additional ticks.
     */
    BAR_SUB("bar_sub");

    private final String label;

    Type(final String label) {
        this.label = label;
    }

    /**
     * Get a type from its label.
     * @param label the type label
     */
    public static Type fromString(final String label) {
        if (label != null) {
            for (Type type : Type.values()) {
                if (label.equalsIgnoreCase(type.label)) {
                    return type;
                }
            }
        }
        return null;
    }

    public final String getLabel() {
        return this.label;
    }
}
