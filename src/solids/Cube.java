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
        vertices.add(new Point3D(size, 0, 0));
        vertices.add(new Point3D(size, size, 0));
        vertices.add(new Point3D(0, size, 0));

        vertices.add(new Point3D(0, 0, size));
        vertices.add(new Point3D(size, 0, size));
        vertices.add(new Point3D(size, size, size));
        vertices.add(new Point3D(0, size, size));

        for (int i = 0; i < 4; i++) { // hrany
            indicesLine.add(i); indicesLine.add((i + 1) % 4);
            indicesLine.add(i); indicesLine.add(i + 4);
            indicesLine.add(i + 4); indicesLine.add((i + 1) % 4 + 4);
        }

        // plochy
        indicesTriangle.add(0); indicesTriangle.add(1); indicesTriangle.add(2); //spodek vpravo
        // TODO zbytek
    }
}
