//----------------------------------------------------------------------------//
//                                                                            //
//                      G l y p h E n v i r o n m e n t                       //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//
// Copyright © Hervé Bitteur and others 2000-2017. All rights reserved.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//----------------------------------------------------------------------------//
// </editor-fold>
package org.audiveris.omr.glyph.facets;

import org.audiveris.omr.lag.Lag;
import org.audiveris.omr.lag.Section;

import org.audiveris.omr.sheet.SystemInfo;

import org.audiveris.omr.util.HorizontalSide;
import org.audiveris.omr.util.Predicate;

import java.awt.Rectangle;
import java.util.Set;

/**
 * Interface {@code GlyphEnvironment} defines the facet in charge of
 * the surrounding environment of a glyph, in terms of staff-based
 * pitch position, of presence of stem or ledgers, etc.
 *
 * @author Hervé Bitteur
 */
interface GlyphEnvironment
        extends GlyphFacet
{
    //~ Methods ----------------------------------------------------------------

    /**
     * Forward stem-related information from the provided glyph
     *
     * @param glyph the glyph whose stem information has to be used
     */
    void copyStemInformation (Glyph glyph);

    /**
     * Report the number of alien pixels, from the provided lag, found
     * in the specified absolute roi
     *
     * @param lag       the lag to serach
     * @param absRoi    the absolute region of interest
     * @param predicate optional predicate to further filter these aliens
     * @return the number of alien pixels found
     */
    int getAlienPixelsFrom (Lag lag,
                            Rectangle absRoi,
                            Predicate<Section> predicate);

    /**
     * Report the set of glyphs that are connected to this one
     *
     * @return the set of neighboring glyphs, connected through their sections
     */
    Set<Glyph> getConnectedNeighbors ();

    /**
     * Report the first stem attached (left then right), if any
     *
     * @return first stem found, or null
     */
    Glyph getFirstStem ();

    /**
     * Report the pitchPosition feature (position relative to the staff)
     *
     * @return the pitchPosition value
     */
    double getPitchPosition ();

    /**
     * Report the stem attached on the provided side, if any
     *
     * @return stem on provided side, or null
     */
    Glyph getStem (HorizontalSide side);

    /**
     * Report the number of stems the glyph is close to
     *
     * @return the number of stems near by, typically 0, 1 or 2.
     */
    int getStemNumber ();

    /**
     * Return the known glyphs stuck on last side of the stick.
     * (this is relevant mainly for a stem glyph)
     *
     * @param predicate the predicate to apply on each glyph
     * @param goods     the set of correct glyphs (perhaps empty)
     * @param bads      the set of non-correct glyphs (perhaps empty)
     */
    void getSymbolsAfter (Predicate<Glyph> predicate,
                          Set<Glyph> goods,
                          Set<Glyph> bads);

    /**
     * Return the known glyphs stuck on first side of the stick.
     * (this is relevant mainly for a stem glyph)
     *
     * @param predicate the predicate to apply on each glyph
     * @param goods     the set of correct glyphs (perhaps empty)
     * @param bads      the set of non-correct glyphs (perhaps empty)
     */
    void getSymbolsBefore (Predicate<Glyph> predicate,
                           Set<Glyph> goods,
                           Set<Glyph> bads);

    /**
     * Report the containing system, if any.
     *
     * @return the system containing this glyph
     */
    SystemInfo getSystem ();

    /**
     * Report whether the glyph touches a ledger
     *
     * @return true if there is a close ledger
     */
    boolean isWithLedger ();

    /**
     * Setter for the pitch position, with respect to containing staff
     *
     * @param pitchPosition the pitch position wrt the staff
     */
    void setPitchPosition (double pitchPosition);

    /**
     * Assign the stem on the provided side
     *
     * @param stem stem glyph
     */
    void setStem (Glyph stem,
                  HorizontalSide side);

    /**
     * Remember the number of stems near by
     *
     * @param stemNumber the number of stems
     */
    void setStemNumber (int stemNumber);

    /**
     * Remember info about ledger nearby
     *
     * @param withLedger true is there is such ledger
     */
    void setWithLedger (boolean withLedger);
}
