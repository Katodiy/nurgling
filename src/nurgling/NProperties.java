package nurgling;

public class NProperties {

    public static class Container extends NProperties{
        public String cap;
        public long free;
        public long full;

        public Container(String cap, long free, long full) {
            this.cap = cap;
            this.free = free;
            this.full = full;
        }

        public Container() {
        }
    }

    public static class Crop extends NProperties{
        public long specstage;
        public long currentStage = 1;
        public long maxstage;

        public Crop(long specstage, long maxstage) {
            this.specstage = specstage;
            this.maxstage = maxstage;
        }

        public Crop() {
        }
    }
}

