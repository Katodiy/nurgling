/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

public class Homing extends Moving {
    public long tgt;
    public Coord2d tc;
    public double v, dist;
    
    public Homing(Gob gob, long tgt, Coord2d tc, double v) {
	super(gob);
	this.tgt = tgt;
	this.tc = tc;
	this.v = v;
    }
    
    public Coord3f getc() {
	Coord2d rc = gob.rc;
	Coord2d tc = this.tc;
	Gob tgt = gob.glob.oc.getgob(this.tgt);
	if(tgt != null)
	    tc = tgt.rc;
	Coord2d d = tc.sub(rc);
	double e = d.abs();
	if(dist > e)
	    rc = tc;
	else if(e > 0.00001)
	    rc = rc.add(d.mul(dist / e));
	return(gob.placer().getc(rc, gob.a));
    }
    
    public double getv() {
	return(v);
    }
    
    public void move(Coord2d c) {
	dist = 0;
    }
    
    public void ctick(double dt) {
	dist += v * (dt * 0.9);
    }

    @OCache.DeltaType(OCache.OD_HOMING)
    public static class $homing implements OCache.Delta {
	public void apply(Gob g, Message msg) {
	    long oid = msg.uint32();
	    if(oid == 0xffffffffl) {
		g.delattr(Homing.class);
	    } else {
		Coord2d tc = msg.coord().mul(OCache.posres);
		double v = msg.int32() * 0x1p-10 * 11;
		Homing homo = g.getattr(Homing.class);
		if((homo == null) || (homo.tgt != oid)) {
		    g.setattr(new Homing(g, oid, tc, v));
		} else {
		    homo.tc = tc;
		    homo.v = v;
		}
	    }
	}
    }

	public Gob tgt(){
		return gob.glob.oc.getgob(this.tgt);
	}

	@Override
	public Coord3f gett() {
		Gob tgt = tgt();
		if(tgt == null) {
			return gob.glob.map.getzp(tc);
		} else {
			return tgt.getc();
		}
	}
}
