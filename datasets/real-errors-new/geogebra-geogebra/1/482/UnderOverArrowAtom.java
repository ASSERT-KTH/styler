/* UnderOverArrowAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.serialize.HasTrueBase;
import com.himamis.retex.renderer.share.xarrows.XLeftArrow;
import com.himamis.retex.renderer.share.xarrows.XLeftRightArrow;
import com.himamis.retex.renderer.share.xarrows.XRightArrow;

/**
 * An atom representing an other atom with an extensible arrow or doublearrow
 * over or under it.
 */
public class UnderOverArrowAtom extends Atom implements HasTrueBase {

	private final Atom base;
	private final boolean over, left, dble;

	public UnderOverArrowAtom(Atom base, boolean left, boolean over) {
		this.base = base;
		this.left = left;
		this.over = over;
		this.dble = false;
	}

	private UnderOverArrowAtom(Atom base, boolean left, boolean over,
			boolean dble) {
		this.base = base;
		this.left = left;
		this.over = over;
		this.dble = dble;
	}

	public UnderOverArrowAtom(Atom base, boolean over) {
		this.base = base;
		this.over = over;
		this.dble = true;
		this.left = false;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		Box b = base != null ? base.createBox(env) : new StrutBox(0, 0, 0, 0);
		double sep = new SpaceAtom(Unit.POINT, 1f, 0, 0)
				.createBox(env).getWidth();
		Box arrow;

		if (dble) {
			arrow = new XLeftRightArrow(b.getWidth());
			sep = 4 * sep;
		} else {
			if (left) {
				arrow = new XLeftArrow(b.getWidth());
			} else {
				arrow = new XRightArrow(b.getWidth());
			}
			sep = -sep;
		}

		VerticalBox vb = new VerticalBox();
		if (over) {
			vb.add(arrow);
			vb.add(new HorizontalBox(b, arrow.getWidth(),
					TeXConstants.Align.CENTER));
			double h = vb.getDepth() + vb.getHeight();
			vb.setDepth(b.getDepth());
			vb.setHeight(h - b.getDepth());
		} else {
			vb.add(new HorizontalBox(b, arrow.getWidth(),
					TeXConstants.Align.CENTER));
			vb.add(new StrutBox(0, sep, 0, 0));
			vb.add(arrow);
			double h = vb.getDepth() + vb.getHeight();
			vb.setDepth(h - b.getHeight());
			vb.setHeight(b.getHeight());
		}

		return vb;

	}

	@Override
	public Atom getTrueBase() {
		return base;
	}
}
