import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class XYZ_v2 {
    public static final int _version = 2;
    public final int version = _version;

    public int x;
    public List<ZYX> y = new ArrayList<>();
    public String z;
    public long a;

    public XYZ_v2(final int x, final List<ZYX> y, final String z, final long a) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.a = a;
    }

    public XYZ_v2() {
    }

    @Override
    public String toString() {
        return "XYZ_v2{" +
                "version=" + version +
                ", x=" + x +
                ", y=" + y +
                ", z='" + z + '\'' +
                ", a=" + a +
                '}';
    }
}
