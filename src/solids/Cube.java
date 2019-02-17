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

        indicesLine.add(0); indicesLine.add(1);
        indicesLine.add(1); indicesLine.add(3);
        indicesLine.add(2); indicesLine.add(3);
        indicesLine.add(2); indicesLine.add(0);

        indicesLine.add(4); indicesLine.add(5);
        indicesLine.add(5); indicesLine.add(7);
        indicesLine.add(6); indicesLine.add(7);
        indicesLine.add(6); indicesLine.add(4);

        indicesLine.add(0); indicesLine.add(4);
        indicesLine.add(1); indicesLine.add(5);
        indicesLine.add(2); indicesLine.add(6);
        indicesLine.add(3); indicesLine.add(7);
    }
}
