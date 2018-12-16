/**
@file
@author Benny Bobaganoosh <thebennybox@gmail.com>
@section LICENSE

Copyright (c) 2014, Benny Bobaganoosh
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.io.IOException;
import java.awt.event.KeyEvent;

/**
 * The sole purpose of this class is to hold the main method.
 *
 * Any other use should be placed in a separate class
 */
public class Main
{
	static boolean DBG = true;

	// Lazy exception handling here. You can do something more interesting 
	// depending on what you're doing
	public static void main(String[] args) throws IOException
	{
		if (Dbg.D) { Dbg.pl(String.format("Yo dude: %s", "hi")); }

		Dbg.testApproxEql();
		TestMatrix4f.test();
		TestVector4f.test();

		Display display = new Display(800, 600, "Software Rendering");
		RenderContext target = display.GetFrameBuffer();

		Bitmap texture = new Bitmap("./res/bricks.jpg");
		Bitmap texture2 = new Bitmap("./res/bricks2.jpg");
		Mesh monkeyMesh = new Mesh("./res/smoothMonkey0.obj");
		Transform monkeyTransform = new Transform(new Vector4f(0,0.0f,3.0f));

		Mesh terrainMesh = new Mesh("./res/terrain2.obj");
		Transform terrainTransform = new Transform(new Vector4f(0,-1.0f,0.0f));

		Matrix4f viewPerspective =
			new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
				(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f);
		if (DBG) Dbg.prtM4f("viewPerspective=", viewPerspective);
		Camera camera = new Camera(viewPerspective);
		
		if (DBG) Dbg.prtM4f("monkeyTransform=", monkeyTransform.GetTransformation());

		Matrix4f mvp = viewPerspective.Mul(monkeyTransform.GetTransformation());
		if (DBG) Dbg.prtM4f("mvp=", mvp);

		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;

			Input input = display.GetInput();
			if(input.GetKey(KeyEvent.VK_ESCAPE)) {
				System.exit(0);
			}
			float xAxisRotation = 0;
			float yAxisRotation = 0;
			float zAxisRotation = 0;
			if(input.GetKey(KeyEvent.VK_LEFT) && input.GetKey(KeyEvent.VK_CONTROL) && (input.GetKeyLocation(KeyEvent.VK_CONTROL) == KeyEvent.KEY_LOCATION_LEFT)) {
				xAxisRotation = (float)Math.toRadians(1);
			}
			if(input.GetKey(KeyEvent.VK_RIGHT) && input.GetKey(KeyEvent.VK_CONTROL) && (input.GetKeyLocation(KeyEvent.VK_CONTROL) == KeyEvent.KEY_LOCATION_LEFT)) {
				xAxisRotation = (float)-Math.toRadians(1);
			}

			if(input.GetKey(KeyEvent.VK_LEFT) && input.GetKey(KeyEvent.VK_SHIFT) && (input.GetKeyLocation(KeyEvent.VK_SHIFT) == KeyEvent.KEY_LOCATION_LEFT)) {
				yAxisRotation = (float)Math.toRadians(1);
			}
			if(input.GetKey(KeyEvent.VK_RIGHT) && input.GetKey(KeyEvent.VK_SHIFT) && (input.GetKeyLocation(KeyEvent.VK_SHIFT) == KeyEvent.KEY_LOCATION_LEFT)) {
				yAxisRotation = (float)-Math.toRadians(1);
			}

			if(input.GetKey(KeyEvent.VK_LEFT) && input.GetKey(KeyEvent.VK_CONTROL) && (input.GetKeyLocation(KeyEvent.VK_CONTROL) == KeyEvent.KEY_LOCATION_RIGHT)) {
				zAxisRotation = (float)Math.toRadians(1);
			}
			if(input.GetKey(KeyEvent.VK_RIGHT) && input.GetKey(KeyEvent.VK_CONTROL) && (input.GetKeyLocation(KeyEvent.VK_CONTROL) == KeyEvent.KEY_LOCATION_RIGHT)) {
				zAxisRotation = (float)-Math.toRadians(1);
			}
			System.out.printf("Y-axis=%4.3f X-axis=%4.3f Z-axis=%4.3f\n", yAxisRotation, xAxisRotation, zAxisRotation);
			monkeyTransform = monkeyTransform.Rotate(new Quaternion(yAxisRotation, xAxisRotation, zAxisRotation));

			Matrix4f vp;
			camera.Update(input, delta);
			vp = camera.GetViewProjection();

			target.Clear((byte)0x00);
			target.ClearDepthBuffer();
			monkeyMesh.Draw(target, vp, monkeyTransform.GetTransformation(), texture2);
			terrainMesh.Draw(target, vp, terrainTransform.GetTransformation(), texture);

			display.SwapBuffers();
		}
	}
}
