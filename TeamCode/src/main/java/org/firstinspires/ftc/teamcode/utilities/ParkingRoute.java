package org.firstinspires.ftc.teamcode.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pure geometry (no FTC imports): shortest route around rectangular keep-out boxes.
 * Builds a visibility graph from the start, the target and every box corner, then runs Dijkstra.
 * Pass boxes already inflated by the robot radius. A box that contains the start is ignored,
 * so a robot that begins touching a FLOWER can still drive away from it.
 */
public final class ParkingRoute {
    private static final double EPS = 1e-9;

    private ParkingRoute() {}

    /**
     * @param boxes each {xMin, yMin, xMax, yMax}
     * @param lo    smallest allowed x or y for a waypoint (wall clearance)
     * @param hi    largest allowed x or y for a waypoint
     * @return waypoints after the start, ending at the target; null if no clear route exists
     */
    public static double[][] plan(double sx, double sy, double tx, double ty,
                                  double[][] boxes, double lo, double hi) {
        List<double[]> active = new ArrayList<>();
        for (double[] b : boxes) {
            if (!(sx > b[0] && sx < b[2] && sy > b[1] && sy < b[3])) active.add(b);
        }

        List<double[]> nodes = new ArrayList<>();
        nodes.add(new double[] {sx, sy});
        nodes.add(new double[] {tx, ty});
        for (double[] b : active) {
            double[][] corners = {{b[0], b[1]}, {b[2], b[1]}, {b[2], b[3]}, {b[0], b[3]}};
            for (double[] c : corners) {
                if (c[0] >= lo && c[0] <= hi && c[1] >= lo && c[1] <= hi) nodes.add(c);
            }
        }

        int n = nodes.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        boolean[] done = new boolean[n];
        Arrays.fill(dist, Double.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[0] = 0;

        for (int step = 0; step < n; step++) {
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!done[i] && (u == -1 || dist[i] < dist[u])) u = i;
            }
            if (u == -1 || dist[u] == Double.MAX_VALUE) break;
            done[u] = true;
            if (u == 1) break;
            for (int v = 0; v < n; v++) {
                if (done[v]) continue;
                double[] a = nodes.get(u), b = nodes.get(v);
                if (!isClear(a[0], a[1], b[0], b[1], active)) continue;
                double d = dist[u] + Math.hypot(b[0] - a[0], b[1] - a[1]);
                if (d < dist[v]) { dist[v] = d; prev[v] = u; }
            }
        }
        if (dist[1] == Double.MAX_VALUE) return null;

        List<double[]> route = new ArrayList<>();
        for (int i = 1; i != 0; i = prev[i]) route.add(0, nodes.get(i));
        return route.toArray(new double[0][]);
    }

    /** True if the segment does not pass through the interior of any box (touching an edge is fine). */
    public static boolean isClear(double x0, double y0, double x1, double y1, List<double[]> boxes) {
        for (double[] box : boxes) {
            if (crossesInterior(x0, y0, x1, y1, box)) return false;
        }
        return true;
    }

    private static boolean crossesInterior(double x0, double y0, double x1, double y1, double[] box) {
        double tMin = 0, tMax = 1;
        double[] p = {x0, y0}, d = {x1 - x0, y1 - y0};
        for (int axis = 0; axis < 2; axis++) {
            double lo = box[axis], hi = box[axis + 2];
            if (Math.abs(d[axis]) < EPS) {
                if (p[axis] <= lo + EPS || p[axis] >= hi - EPS) return false;
            } else {
                double t1 = (lo - p[axis]) / d[axis], t2 = (hi - p[axis]) / d[axis];
                tMin = Math.max(tMin, Math.min(t1, t2));
                tMax = Math.min(tMax, Math.max(t1, t2));
            }
        }
        return tMax - tMin > EPS;
    }

    public static void main(String[] args) {
        double[][] boxes = {{30, 38, 114, 106}, {0, 31, 19, 65}};
        List<double[]> list = Arrays.asList(boxes);
        // direct line is clear
        assertTrue(plan(25, 10, 25, 100, boxes, 11, 133).length == 1);
        // diagonal across the center must detour, and every leg must avoid every box
        double[][] r = plan(10, 10, 130, 130, boxes, 11, 133);
        assertTrue(r.length >= 2);
        double px = 10, py = 10;
        for (double[] w : r) { assertTrue(isClear(px, py, w[0], w[1], list)); px = w[0]; py = w[1]; }
        // starting inside a box (touching a FLOWER) still routes out
        assertTrue(plan(12, 48, 12, 108, boxes, 11, 133) != null);
        // target boxed in by an unreachable wall of boxes has no route
        double[][] walled = {{20, 20, 40, 120}, {20, 100, 120, 120}, {100, 20, 120, 120}, {20, 20, 120, 40}};
        assertTrue(plan(130, 130, 70, 70, walled, 11, 133) == null);
        System.out.println("ParkingRoute OK");
    }

    private static void assertTrue(boolean ok) {
        if (!ok) throw new AssertionError();
    }
}
