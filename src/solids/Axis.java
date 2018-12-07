package solids;

import java.awt.Color;

import transforms.Point3D;

public class Axis extends SolidBase {

    public Axis(){
        vertices.add(new Point3D(0,0,0));
        vertices.add(new Point3D(1,0,0));
        vertices.add(new Point3D(0,1,0));
        vertices.add(new Point3D(0,0,1));

        indices.add(0); indices.add(1);
        indices.add(0); indices.add(2);
        indices.add(0); indices.add(3);
    }

    @Override
    public int getColorByEdge(int index) {
        switch (index){
            case 0: return Color.RED.getRGB();
            case 1: return Color.GREEN.getRGB();
            case 2: return Color.BLUE.getRGB();
        }
        return super.getColorByEdge(index);
    }
}
