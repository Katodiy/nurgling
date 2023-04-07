/* Preprocessed source code */
/* $use: lib/globfx */

package haven.res.lib.env;

import haven.*;
import java.util.*;
import haven.res.lib.globfx.*;

@FromResource(name = "lib/env", version = 12)
public class Environ extends GlobData {
    private final Random rnd = new Random();
    
    float wdir = rnd.nextFloat() * (float)Math.PI * 2;
    float wvel = rnd.nextFloat() * 3;
    Coord3f gust = new Coord3f(0, 0, 0);
    Coord3f wind = Coord3f.o;
    private void wind(float dt) {
	Coord3f base = Coord3f.o.sadd(0, wdir, wvel);
	wdir += ((rnd.nextFloat() * 2) - 1) * 0.005;
	if(wdir < 0)
	    wdir += (float)Math.PI * 2;
	if(wdir > Math.PI * 2)
	    wdir -= (float)Math.PI * 2;
	wvel += rnd.nextFloat() * 0.005;
	if(wvel < 0)
	    wvel = 0;
	if(wvel > 20)
	    wvel = 20;
	if(rnd.nextInt(2000) == 0) {
	    gust.x = rnd.nextFloat() * 200 - 100;
	    gust.y = rnd.nextFloat() * 200 - 100;
	}
	float df = (float)Math.pow(0.2, dt);
	gust.x *= df;
	gust.y *= df;
	gust.z *= df;
	wind = base.add(gust);
    }
    
    public Coord3f wind() {
	return(wind);
    }

    public boolean tick(float dt) {
	wind(dt);
	return(false);
    }

    public static Environ get(Glob glob) {
	return(GlobEffector.getdata(glob, new Environ()));
    }
}
