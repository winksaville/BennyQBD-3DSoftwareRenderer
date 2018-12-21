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

class RotationInput {

	int plusActionKey;
	int negActionKey;
	ModifierKeyType modifierKeyType;
	int modifierKey;
	float deltaDegrees;

	boolean changed;
	boolean pressedPlus;
	boolean pressedNeg;
	float rotation;

	RotationInput(float deltaDegrees, int plusActionKey, int negActionKey, ModifierKeyType modifierKeyType, int modifierKey) {
		this.deltaDegrees = deltaDegrees;
		this.plusActionKey = plusActionKey;
		this.negActionKey = negActionKey;
		this.modifierKeyType = modifierKeyType;
		this.modifierKey = modifierKey;
		this.changed = false;
		this.pressedPlus = false;
		this.pressedNeg = false;
		this.rotation = 0;
	}

	RotationInput(float deltaDegrees, int plusActionKey, int negActionKey) {
		this.deltaDegrees = deltaDegrees;
		this.plusActionKey = plusActionKey;
		this.negActionKey = negActionKey;
		this.modifierKeyType = ModifierKeyType.None;
		this.modifierKey = 0;
		this.changed = false;
		this.pressedPlus = false;
		this.pressedNeg = false;
		this.rotation = 0;
	}

	void update(Input input) {
		// Need to choose GetKeyLeft | GetKeyRight
		if(!this.pressedPlus && input.GetKey(this.plusActionKey)) {
			switch (modifierKeyType) {
				case None:
					this.pressedPlus = true;
					break;
				case Left:
					if(input.GetKeyLeft(this.modifierKey)) {
						this.pressedPlus = true;
					}
					break;
				case Right:
					if(input.GetKeyRight(this.modifierKey)) {
						this.pressedPlus = true;
					}
					break;
			}
		}
		if(this.pressedPlus && !input.GetKey(this.plusActionKey)) {
			this.pressedPlus = false;
			this.changed = true;
			this.rotation = (float)Math.toRadians(this.deltaDegrees);
			System.out.printf("RotationInput X+ %4.3f\n", this.rotation);
		}
		if(!this.pressedNeg && input.GetKey(this.negActionKey)) {
			switch (modifierKeyType) {
				case None:
					this.pressedNeg = true;
					break;
				case Left:
					if(input.GetKeyLeft(this.modifierKey)) {
						this.pressedNeg = true;
					}
					break;
				case Right:
					if(input.GetKeyRight(this.modifierKey)) {
						this.pressedNeg = true;
					}
					break;
			}
		}
		if(this.pressedNeg && !input.GetKey(this.negActionKey)) {
			this.pressedNeg = false;
			this.changed = true;
			this.rotation = (float)-Math.toRadians(this.deltaDegrees);
			System.out.printf("RotationInput X- %4.3f\n", this.rotation);
		}
	}

	boolean changed() {
		return this.changed;
	}

	float getRotation() {
		this.changed = false;
		float value = this.rotation;
		System.out.printf("RotationInput.getRotation = %4.3f\n", value);
		this.rotation = 0;
		return value;
	}
}

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
		Transform monkeyTransform = new Transform(new Vector4f(0,0.0f,0.0f));

		Mesh terrainMesh = new Mesh("./res/terrain2.obj");
		Transform terrainTransform = new Transform(new Vector4f(0,-1.0f,0.0f));

		Matrix4f cameraViewPerspective =
			new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
				(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f);
		Vector4f cameraPosition = new Vector4f(1, 1, 3);
		Vector4f cameraLookAt = new Vector4f(0, 0, 0);
		Camera camera = new Camera(cameraViewPerspective, cameraPosition, cameraLookAt);
		if (DBG) {
			Dbg.prtV4f("cameraPosition=", cameraPosition); Dbg.p("\n");
			Dbg.prtV4f("cameraLookAt=", cameraLookAt); Dbg.p("\n");
			Dbg.prtM4f("cameraViewPerspective=", cameraViewPerspective);
			Dbg.prtM4f("monkeyTransform=", monkeyTransform.GetTransformation());
		}

		Matrix4f mvp = cameraViewPerspective.Mul(monkeyTransform.GetTransformation());
		if (DBG) Dbg.prtM4f("mvp=", mvp);

		RotationInput xRotationInput = new RotationInput(10.0f, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, ModifierKeyType.Left, KeyEvent.VK_CONTROL);
		RotationInput yRotationInput = new RotationInput(10.0f, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, ModifierKeyType.Left, KeyEvent.VK_SHIFT);
		RotationInput zRotationInput = new RotationInput(10.0f, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, ModifierKeyType.Right, KeyEvent.VK_CONTROL);

		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		while(true)
		{
			Matrix4f mtm4f;
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;

			Input input = display.GetInput();
			if(input.GetKey(KeyEvent.VK_ESCAPE)) {
				System.exit(0);
			}

			xRotationInput.update(input);
			yRotationInput.update(input);
			zRotationInput.update(input);

			if (xRotationInput.changed() || yRotationInput.changed() || zRotationInput.changed()) {
				float xAxisRotation = xRotationInput.getRotation();
				float yAxisRotation = yRotationInput.getRotation();
				float zAxisRotation = zRotationInput.getRotation();

				System.out.printf("Y-axis=%4.3f X-axis=%4.3f Z-axis=%4.3f\n", yAxisRotation, xAxisRotation, zAxisRotation);
				monkeyTransform = monkeyTransform.Rotate(new Quaternion(yAxisRotation, xAxisRotation, zAxisRotation));
				mtm4f = monkeyTransform.GetTransformation();
				Dbg.prtM4f("mtf4f:\n", mtm4f);
			} else {
				mtm4f = monkeyTransform.GetTransformation();
			}

			Matrix4f vp;
			camera.Update(input, delta);
			vp = camera.GetViewProjection();

			target.Clear((byte)0x00);
			target.ClearDepthBuffer();
			monkeyMesh.Draw(target, vp, mtm4f, texture2);
			terrainMesh.Draw(target, vp, terrainTransform.GetTransformation(), texture);

			display.SwapBuffers();
		}
	}
}
