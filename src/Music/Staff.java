package Music;

import GraphicsLib.I;
import GraphicsLib.UC;
import Reaction.Gesture;
import Reaction.Mass;
import Reaction.Reaction;
import Sandbox.Music1;

import java.awt.*;

import static Sandbox.Music1.PAGE;

public class Staff extends Mass {

    public Sys sys;
    public int ndx;

    public int H() {
        return sysFmt().get(ndx).H;
    }

    public Staff(Sys sys) {
        super("BACK");
        this.sys = sys;
        this.ndx = sys.getStaffSize();
        sys.addStaff(this);
        addReaction(new Reaction("S-S") {//creates a bar
            @Override
            public int bid(Gesture gesture) {

                int x = gesture.vs.midx(), y1 = gesture.vs.loy(), y2 = gesture.vs.hiy();
                if (x < PAGE.left || x > PAGE.right + UC.barToMarginSnap) {
                    return UC.noBid;
                }
                int dt = Math.abs(y1 - Staff.this.yTop());
                int db = Math.abs(y2 - Staff.this.yBot());
                if (dt > 15 || db > 15) {//15 is about 2inches 2 staff line
                    return UC.noBid;
                } else {
                    return dt + db + 20;
                }
            }

            @Override
            public void act(Gesture gesture) {
                new Bar(Staff.this.sys, gesture.vs.midx());
            }
        });

        addReaction(new Reaction("S-S") { // toggle BarContinues
            public int bid(Gesture g) {
                if (Staff.this.sys.ndx != 0) {
                    return UC.noBid;
                } // we only change bar continues in first system
                int y1 = g.vs.loy(), y2 = g.vs.hiy();
                if (Staff.this.ndx == sysFmt().size() - 1) {
                    return UC.noBid;
                } // last staff in sys can't continue
                if (Math.abs(y1 - Staff.this.yBot()) > 20) {
                    return UC.noBid;
                }
                Staff nextStaff = sys.getStaff(ndx + 1);
                if (Math.abs(y2 - nextStaff.yTop()) > 20) {
                    return UC.noBid;
                }
                return 10;
            }

            public void act(Gesture g) {
                sysFmt().get(Staff.this.ndx).toggleBarContinues();
            }
        });

        addReaction(new Reaction("SW-SW") {
            @Override
            public int bid(Gesture gesture) {
                I.Page page = sys.page;
                int x = gesture.vs.midx();
                int y = gesture.vs.midy();
                if (x < page.left() || x > page.right()) {
                    return UC.noBid;
                }
                int top = Staff.this.yTop();
                int bot = Staff.this.yBot();
                if (y < top || y > bot) {
                    return UC.noBid;
                }
                return 20;
            }

            @Override
            public void act(Gesture gesture) {
                new Head(Staff.this, gesture.vs.midx(), gesture.vs.midy());
            }
        });

        addReaction(new Reaction("E-S") {
            @Override
            public int bid(Gesture g) {
                I.Page page = sys.page;
                int x = g.vs.midx();
                int y = g.vs.midy();
                if (x < page.left() || x > page.right()) {
                    return UC.noBid;
                }
                int top = Staff.this.yTop();
                int bot = Staff.this.yBot();
                if (y < top || y > bot) {
                    return UC.noBid;
                }
                return 20;
            }

            @Override
            public void act(Gesture g) {
                new Rest(Staff.this, Staff.this.sys.getTime(g.vs.midx())).nFlag++;
            }
        });
    }

    public Sys.Fmt sysFmt() {
        return sysFmt();
    }

    @Override
    public void show(Graphics g) {

    }

    public int yTop() {
        return sys.yTop() + sysFmt().get(ndx).dy;
    }

    public int yBot() {
        return yTop() + sysFmt().get(ndx).height();
    }

    public int yLine(int line) {
        return yTop() + line * H();
    }

    public static class Fmt {

        public boolean barContinues = false;
        public int nLines = 5;
        public int H;
        public int dy = 0;

        public Fmt(int dy, I.Page page) {
            this.dy = dy;
            this.H = page.sysFmt().maxH;
            page.sysFmt().add(this);
        }

        public void toggleBarContinues() {
            barContinues = !barContinues;
        }

        public int height() {
            return (nLines - 1) * 2 * H;
        }

        public void showAt(Graphics g, int y, I.Page page) {
            for (int i = 0; i < nLines; i++) {
                int yy = y + 2 * i * H;
                g.drawLine(page.left(), yy, page.right(), yy);
            }
        }
    }
}