/* Preprocessed source code */
package haven.res.lib.climb;

import haven.*;
import haven.render.*;
import java.util.*;
import haven.Skeleton.*;

@FromResource(name = "lib/climb", version = 4)
public class UnOffset extends TrackMod {
    public final float[] ft;
    public final float[][] toff;
    public Location off = new Location(Matrix4f.identity());
    public float tmod = 1.0f;

    private UnOffset(ModOwner owner, Skeleton skel, Track[] tracks, FxTrack[] fx, float[] ft, float[][] toff, float len, WrapMode mode) {
	skel.super(owner, tracks, fx, len, mode);
	this.ft = ft;
	this.toff = toff;
	aupdate(0.0f);
    }

    public void aupdate(float time) {
	super.aupdate(time);
	if(ft == null)
	    return;
	Coord3f trans;
	if(ft.length == 1) {
	    trans = new Coord3f(toff[0][0], toff[0][1], toff[0][2]);
	} else {
	    float[] cf, nf;
	    float ct, nt;
	    int l = 0, r = ft.length;
	    while(true) {
		/* c should never be able to be >= frames.length */
		int c = l + ((r - l) >> 1);
		ct = ft[c];
		nt = (c < ft.length - 1)?(ft[c + 1]):len;
		if(ct > time) {
		    r = c;
		} else if(nt < time) {
		    l = c + 1;
		} else {
		    cf = toff[c];
		    nf = toff[(c + 1) % toff.length];
		    break;
		}
	    }
	    float d;
	    if(nt == ct)
		d = 0;
	    else
		d = (time - ct) / (nt - ct);
	    trans = new Coord3f(cf[0] + ((nf[0] - cf[0]) * d),
				cf[1] + ((nf[1] - cf[1]) * d),
				cf[2] + ((nf[2] - cf[2]) * d));
	}
	off = Location.xlate(trans);
    }

    /* XXX: It should be possible to subclass UnOffset */
    public boolean tick(float dt) {
	float tmod = this.tmod;
	if(tmod < 0)
	    tmod = 0;
	return(super.tick(dt * tmod));
    }

    public static UnOffset forres(ModOwner owner, Skeleton skel, ResPose pose, WrapMode mode) {
	if(mode == null)
	    mode = pose.defmode;
	Track[] tracks = new Track[pose.tracks.length];
	int tn = 0;
	List<Track> roots = new ArrayList<Track>(pose.tracks.length);
	for(int i = 0; i < tracks.length; i++) {
	    Track t = pose.tracks[i];
	    Bone b = skel.bones.get(t.bone);
	    if(b.parent == null)
		roots.add(t);
	    else
		tracks[tn++] = pose.tracks[i];
	}

	float[] ft = new float[roots.get(0).frames.length];
	for(int i = 0; i < roots.get(0).frames.length; i++)
	    ft[i] = roots.get(0).frames[i].time;
	float[][] toff = new float[ft.length][3];
	for(Track t : roots) {
	    if(t.frames.length != ft.length)
		throw(new RuntimeException("Deviant root track; has " + t.frames.length + " frames, not " + ft.length));
	    for(int i = 0; i < ft.length; i++) {
		Track.Frame f = t.frames[i];
		if(f.time != ft[i])
		    throw(new RuntimeException("Deviant root track; has frame " + i + " at " + f.time + " s, not " + ft[i] + " s"));
		toff[i][0] += f.trans[0];
		toff[i][1] += f.trans[1];
		toff[i][2] += f.trans[2];
	    }
	}
	float ninv = 1.0f / roots.size();
	for(int i = 0; i < ft.length; i++) {
	    toff[i][0] *= ninv;
	    toff[i][1] *= ninv;
	    toff[i][2] *= ninv;
	}
	for(Track t : roots) {
	    Track.Frame[] nf = new Track.Frame[ft.length];
	    for(int i = 0; i < ft.length; i++) {
		float[] trans = new float[3];
		for(int o = 0; o < 3; o++)
		    trans[o] = t.frames[i].trans[o] - toff[i][o];
		nf[i] = new Track.Frame(ft[i], trans, t.frames[i].rot);
	    }
	    tracks[tn++] = new Track(t.bone, nf);
	}

	Track[] remap = new Track[skel.blist.length];
	for(Track t : tracks)
	    remap[skel.bones.get(t.bone).idx] = t;
	UnOffset ret = new UnOffset(owner, skel, remap, pose.effects, ft, toff, pose.len, mode);
	return(ret);
    }
}
