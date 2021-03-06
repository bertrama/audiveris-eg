//----------------------------------------------------------------------------//
//                                                                            //
//                              T e x t T a s k                               //
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
package org.audiveris.omr.script;

import org.audiveris.omr.glyph.Evaluation;
import org.audiveris.omr.glyph.facets.Glyph;

import org.audiveris.omr.sheet.Sheet;

import org.audiveris.omr.text.TextRoleInfo;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Class {@code TextTask} records the assignment of textual
 * characteristics to a collection of glyphs.
 *
 * @author Hervé Bitteur
 */
public class TextTask
        extends GlyphUpdateTask
{
    //~ Instance fields --------------------------------------------------------

    /** Role of the textual glyph */
    // TODO: Define proper mapping for RoleInfo class
    @XmlElement(name = "role-info")
    private final TextRoleInfo roleInfo;

    /** String content of the textual glyph */
    @XmlAttribute
    private final String content;

    //~ Constructors -----------------------------------------------------------
    //----------//
    // TextTask //
    //----------//
    /**
     * Creates a new TextTask object.
     *
     * @param sheet    the sheet impacted
     * @param roleInfo the role of this text item
     * @param content  The content as a string
     * @param glyphs   the impacted glyph(s)
     */
    public TextTask (Sheet sheet,
                     TextRoleInfo roleInfo,
                     String content,
                     Collection<Glyph> glyphs)
    {
        super(sheet, glyphs);
        this.roleInfo = roleInfo;
        this.content = content;
    }

    //----------//
    // TextTask //
    //----------//
    /** No-arg constructor for JAXB only */
    private TextTask ()
    {
        roleInfo = null;
        content = null;
    }

    //~ Methods ----------------------------------------------------------------
    //------//
    // core //
    //------//
    @Override
    public void core (Sheet sheet)
            throws Exception
    {
        sheet.getSymbolsController()
                .getModel()
                .assignText(
                getInitialGlyphs(),
                roleInfo,
                content,
                Evaluation.MANUAL);
    }

    //-----------------//
    // internalsString //
    //-----------------//
    @Override
    protected String internalsString ()
    {
        StringBuilder sb = new StringBuilder(super.internalsString());
        sb.append(" text");

        sb.append(" ")
                .append(roleInfo.role);

        if (roleInfo.creatorType != null) {
            sb.append(" ")
                    .append(roleInfo.creatorType);
        }

        sb.append(" \"")
                .append(content)
                .append("\"");

        return sb.toString();
    }
}
