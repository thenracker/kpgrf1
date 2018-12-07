package solids;

import java.util.ArrayList;
import java.util.List;

import transforms.Point3D;

public abstract class SolidBase implements Solid {

    protected List<Point3D> vertices;
    protected List<Integer> indices;

    public SolidBase(){
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
    }

    @Override
    public List<Point3D> getVertices() {
        return vertices;
    }

    @Override
    public List<Integer> getIndices() {
        return indices;
    }
}
