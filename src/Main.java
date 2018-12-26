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
enum ModifierKeyType { None, Left, Right }

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

		Display display = new Display(1024, 1024, "Software Rendering");
		RenderContext target = display.GetFrameBuffer();

		Bitmap texture = new Bitmap("./res/bricks.jpg");
		Bitmap texture2 = new Bitmap("./res/bricks2.jpg");

		Mesh monkeyMesh = new Mesh("./res/smoothMonkey0.obj");
		Vector4f monkeyLocationPoint = new Vector4f(0, 0, 0);
		Vector4f monkeyLookAtPoint = new Vector4f(0, 0, 1);
		Vector4f monkeyUpAxis = new Vector4f(0, 1, 0);
		Entity monkeyEntity = new Entity(monkeyMesh, monkeyLocationPoint, monkeyLookAtPoint, monkeyUpAxis);

		Mesh terrainMesh = new Mesh("./res/terrain2.obj");
		Transform terrainTransform = new Transform(new Vector4f(0, -1, 0));

		Matrix4f cameraViewPerspective =
			new Matrix4f().InitPerspective((float)Math.toRadians(90.0f),
				(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f);
		Vector4f cameraPosition = new Vector4f(0, 0, -3);
		Vector4f cameraLookAt = new Vector4f(0, 0, 0);
		Camera camera = new Camera(cameraViewPerspective, cameraPosition, cameraLookAt);
		if (DBG) {
			Dbg.prtV4f("cameraPosition:", cameraPosition); Dbg.p("\n");
			Dbg.prtV4f("cameraLookAt:", cameraLookAt); Dbg.p("\n");
			Dbg.prtM4f("cameraViewPerspective:", cameraViewPerspective);
			Dbg.prtM4f("monkey matrix:", monkeyEntity.GetTransform().GetTransformation());
		}

		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		boolean focusCamera = false;
		while(true)
		{
			Matrix4f mtm4f;
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime) / 1000000000.0);
			previousTime = currentTime;

			Input input = display.GetInput();
			if(input.GetKey(KeyEvent.VK_ESCAPE)) {
				System.exit(0);
			} else if(input.GetKey(KeyEvent.VK_M)) {
				focusCamera = false;
			} else if(input.GetKey(KeyEvent.VK_C)) {
				focusCamera = true;
			}

			if (focusCamera) {
				camera.Update(input, currentTime, delta);
			} else {
				float rotationDelta = 5.0f * delta;
				float translationDelta = 2.0f * delta;
				monkeyEntity.Update(input, currentTime, rotationDelta, translationDelta);
			}
			Matrix4f vp = camera.GetViewProjection();


			target.Clear((byte)0x00);
			target.ClearDepthBuffer();

			monkeyEntity.GetMesh().Draw(target, vp, monkeyEntity.GetTransform().GetTransformation(), texture2);
			terrainMesh.Draw(target, vp, terrainTransform.GetTransformation(), texture);

			display.SwapBuffers();
		}
	}
}
