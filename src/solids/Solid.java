package solids;

import java.awt.Color;
import java.util.List;

import transforms.Point3D;

public interface Solid {

    // vertex buffer
    List<Point3D> getVertices();

    // index buffer
    List<Integer> getIndices(Primitive primitive);

    default int getColorByEdge(int index){
        return Color.BLACK.getRGB();
    }

}
