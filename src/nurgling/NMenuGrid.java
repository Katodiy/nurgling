package nurgling;

import haven.*;

import java.awt.event.KeyEvent;
import java.util.*;

public class NMenuGrid extends MenuGrid {
    public Pagina lastCraft = null;

    public class CustomPagButton extends PagButton {

        private final CustomPaginaAction action;

        public CustomPagButton(Pagina pag, CustomPaginaAction action) {
            super(pag);
            this.action = action;
        }

        @Override
        public void use() {
            action.perform(pag);
        }

        @Override
        public void use(Interaction iact) {
            action.perform(pag);
        }
    }

    public interface CustomPaginaAction {
        void perform(Pagina ctx);
    }

    private Map<Character, PagButton> hotmap = new TreeMap<Character, PagButton>();
    
    public NMenuGrid( ) {
    }
    
    public void reset(){
        this.cur = null;
        curoff = 0;
        updlayout();
    }
    
    @Override
    protected void updlayout () {
        synchronized(paginae) {
            List<PagButton> cur = new ArrayList<>();
            recons = !cons(this.cur, cur);
            Collections.sort(cur, Comparator.comparing(PagButton::sortkey));
            this.curbtns = cur;
            int i = curoff;
            hotmap.clear();
            for(int y = 0; y < gsz.y; y++) {
                for(int x = 0; x < gsz.x; x++) {
                    PagButton btn = null;
                    if((this.cur != null) && (x == gsz.x - 1) && (y == gsz.y - 1)) {
                        btn = bk;
                    } else if((cur.size() > ((gsz.x * gsz.y) - 1)) && (x == gsz.x - 2) && (y == gsz.y - 1)) {
                        btn = next;
                    } else if(i < cur.size()) {
                        Resource.AButton ad = cur.get(i).res.layer(Resource.action);
                        if (ad.hk != 0)
                            hotmap.put(Character.toUpperCase(ad.hk), cur.get(i));
                        btn = cur.get(i++);
                    }
                    layout[x][y] = btn;
                }
            }
        }
    }

    public boolean globtype(char k, KeyEvent ev) {
        if(kb_next.key().match(ev) && (layout[gsz.x - 2][gsz.y - 1] == next)) {
            use(next,new Interaction(),  false);
            return(true);
        }else
        if(kb_root.key().match(ev) && (this.cur != null)) {
            change(null);
            return(true);
        } else if(kb_back.key().match(ev) && (this.cur != null)) {
            use(bk, new Interaction(), false);
            return(true);
        } else if(kb_next.key().match(ev) && (this.cur != null)) {
            use(next, new Interaction(), false);
            return(true);
        }
        int cp = -1;
        PagButton pag = null;
        for(PagButton btn : curbtns) {
            if(btn.bind.key().match(ev)) {
                int prio = btn.bind.set() ? 1 : 0;
                if((pag == null) || (prio > cp)) {
                    pag = btn;
                    cp = prio;
                }
            }
        }
        if(pag != null) {
            use(pag, new Interaction(), ( KeyMatch.mods(ev) & KeyMatch.S) == 0);
            if(this.cur != null)
                showkeys = true;
            return(true);
        }
        return(false);
    }

    public boolean globtype(char k) {
        PagButton r;
        if(k=='q'){
            this.cur = null;
            curoff = 0;
            updlayout();}
        else {
            r = hotmap.get(Character.toUpperCase(k));
            if (r != null) {
                use(r,new Interaction(),  true);
                return (true);
            }
        }
        return (false);
    }

    public Pagina getParent(Pagina p){
        if(p == null){
            return null;
        }
        try {
            Resource res = p.res();
            Resource.AButton ad = res.layer(Resource.action);
            if (ad == null)
                return null;
            Pagina parent = paginafor(ad.parent);
            return (parent == p) ? null : parent;
        } catch (Loading e){
            return null;
        }
    }

    public boolean isCrafting(Pagina p) {
        return (p != null) && (isCrafting(p.res()) || isCrafting(getParent(p)));
    }

    public boolean isCrafting(Resource res){
        return res.name.contains("paginae/act/craft");
    }

    @Override
    public void use(PagButton r, Interaction iact, boolean reset) {
        if(isCrafting(r.pag)) {
            lastCraft = r.pag;
        }
        super.use(r,iact,reset);
    }

    public Pagina paginafor(Resource res) {
        if(res != null) {
            synchronized (pmap) {
                for (Indir<Resource> key : pmap.keySet()) {
                    if(Objects.equals(key.get().name, res.name)) { return pmap.get(key); }
                }
            }
        }
        return null;
    }
}
