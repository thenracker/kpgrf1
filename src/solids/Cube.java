package solids;

import transforms.Point3D;

public class Cube extends SolidBase {

		/*
           7______6
		   /|    /|
		 4/_|___/5|
		 | 3|__|_ |2
		 | /   | /
		 |/____|/
		 0     1
		 */

    public Cube(double size){
        vertices.add(new Point3D(0, 0, 0));
        vertices.add(new Point3D(0, size, 0));
        vertices.add(new Point3D(size, 0, 0));
        vertices.add(new Point3D(size, size, 0));
        vertices.add(new Point3D(0, 0, size));
        vertices.add(new Point3D(0, size, size));
        vertices.add(new Point3D(size, 0, size));
        vertices.add(new Point3D(size, size, size));

        indices.add(0); indices.add(1);
        indices.add(1); indices.add(3);
        indices.add(2); indices.add(3);
        indices.add(2); indices.add(0);

        indices.add(4); indices.add(5);
        indices.add(5); indices.add(7);
        indices.add(6); indices.add(7);
        indices.add(6); indices.add(4);

        indices.add(0); indices.add(4);
        indices.add(1); indices.add(5);
        indices.add(2); indices.add(6);
        indices.add(3); indices.add(7);
    }
}
