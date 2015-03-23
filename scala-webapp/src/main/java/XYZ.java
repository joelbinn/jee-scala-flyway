/*
 * NYPS 2020
 * 
 * User: joel
 * Date: 2015-02-26
 * Time: 21:11
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class XYZ {
    private static final int _version = 1;
    public int x;
    public List<ZYX> y = new ArrayList<>();
    public String z;
    public final int version = _version;

    public XYZ() {
    }

    public XYZ(final int x, final List<ZYX> y, final String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "XYZ{" +
                "x=" + x +
                ", y=" + y +
                ", z='" + z + '\'' +
                '}';
    }
}
