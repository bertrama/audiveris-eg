//----------------------------------------------------------------------------//
//                                                                            //
//                             P a g e s S t e p                              //
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
package org.audiveris.omr.step;

import org.audiveris.omr.constant.Constant;
import org.audiveris.omr.constant.ConstantSet;

import org.audiveris.omr.score.ScoreChecker;
import org.audiveris.omr.score.ScoreCleaner;
import org.audiveris.omr.score.TimeSignatureFixer;
import org.audiveris.omr.score.TimeSignatureRetriever;
import org.audiveris.omr.score.entity.Page;
import org.audiveris.omr.score.entity.ScoreSystem;

import org.audiveris.omr.sheet.Sheet;
import org.audiveris.omr.sheet.SystemInfo;

import org.audiveris.omr.util.WrappedBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Class {@code PagesStep} translates glyphs into score entities for
 * a page.
 *
 * @author Hervé Bitteur
 */
public class PagesStep
        extends AbstractSystemStep
{
    //~ Static fields/initializers ---------------------------------------------

    /** Specific application parameters */
    private static final Constants constants = new Constants();

    /** Usual logger utility */
    private static final Logger logger = LoggerFactory.getLogger(
            PagesStep.class);

    //~ Constructors -----------------------------------------------------------
    //-----------//
    // PagesStep //
    //-----------//
    /**
     * Creates a new PagesStep object.
     */
    public PagesStep ()
    {
        super(
                Steps.PAGES,
                Level.SHEET_LEVEL,
                Mandatory.MANDATORY,
                DATA_TAB,
                "Translate glyphs to score items");
    }

    //~ Methods ----------------------------------------------------------------
    //-----------//
    // displayUI //
    //-----------//
    @Override
    public void displayUI (Sheet sheet)
    {
        // Since we may have purged slots, let's reset highlighted slot if any
        sheet.getSymbolsEditor()
                .highLight(null);

        Steps.valueOf(Steps.SYMBOLS)
                .displayUI(sheet);
    }

    //----------//
    // doSystem //
    //----------//
    @Override
    public void doSystem (SystemInfo system)
            throws StepException
    {
        final int iterMax = constants.maxPageIterations.getValue();
        final ScoreSystem scoreSystem = system.getScoreSystem();
        final WrappedBoolean modified = new WrappedBoolean(true);

        // Purge system of non-active glyphs
        system.removeInactiveGlyphs();

        for (int iter = 1; modified.isSet() && (iter <= iterMax); iter++) {
            modified.set(false);
            logger.debug(
                    "System#{} translation iter #{}",
                    system.getId(),
                    iter);

            // Clear errors for this system only (and this step)
            clearSystemErrors(system);

            // Cleanup the system, staves, measures, barlines, ...
            // and clear glyph (& sentence) translations
            scoreSystem.accept(new ScoreCleaner());

            // Real translation
            system.translateSystem();

            // Final checks at system level 
            scoreSystem.acceptChildren(new ScoreChecker(modified));
        }
    }

    //----------//
    // doEpilog //
    //----------//
    @Override
    protected void doEpilog (Collection<SystemInfo> systems,
                             Sheet sheet)
            throws StepException
    {
        // For the very first time, we reperform from the SYMBOLS step
        if (!sheet.isDone(this)) {
            sheet.done(this);

            // Reperform SYMBOLS once
            try {
                Stepping.reprocessSheet(
                        Steps.valueOf(Steps.SYMBOLS),
                        sheet,
                        systems,
                        true);
            } catch (Exception ex) {
                logger.warn("Error in re-processing from " + this, ex);
            }
        } else {
            // Final cross-system translation tasks
            if (systems == null) {
                systems = sheet.getSystems();
            }

            if (!systems.isEmpty()) {
                systems.iterator()
                        .next()
                        .translateFinal();

                // Finally, all actions for completed page (in proper order)
                Page page = sheet.getPage();

                // 1/ Look carefully for time signatures
                page.accept(new TimeSignatureRetriever());

                // 2/ Adapt time sigs to intrinsic measure & chord durations
                page.accept(new TimeSignatureFixer());
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------
    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
            extends ConstantSet
    {
        //~ Instance fields ----------------------------------------------------

        private final Constant.Integer maxPageIterations = new Constant.Integer(
                "count",
                2,
                "Maximum number of iterations for PAGES task");

    }
}
