/* Copyright (c) 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/*
 *
 * This is an example LinearOpMode that shows how to use
 * a Modern Robotics Color Sensor.
 *
 * The op mode assumes that the color sensor
 * is configured with a name of "mr".
 *
 * You can use the X button on either gamepad to turn the LED on and off.
 *
 */
public class    MRColor extends LinearOpMode {

  ColorSensor sensorRGBL;
  ColorSensor sensorRGBR;

  String leftGuess;
  String rightGuess;

  @Override
  public void runOpMode() throws InterruptedException {
    // write some device information (connection info, name and type)
    // to the log file.
    hardwareMap.logDevices();

    // get a reference to our ColorSensor object.
    sensorRGBL = hardwareMap.colorSensor.get("MRColor_left");
    sensorRGBR = hardwareMap.colorSensor.get("MRColor_right");

    // Change one color sensor to a new address
    // A core device discovery program was used to change this.
    sensorRGBR.setI2cAddress(0x70);

    // bEnabled represents the state of the LED.
    //boolean bEnabled = true;

    // turn the LED on in the beginning, just so user will know that the sensor is active.
    sensorRGBR.enableLed(true);
    sensorRGBL.enableLed(true);

    // wait one cycle.
    waitOneFullHardwareCycle();

    // wait for the start button to be pressed.
    waitForStart();

    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvRValues[] = {0F,0F,0F};
    float hsvLValues[] = {0F,0F,0F};

    // values is a reference to the hsvValues array.
    //final float valuesR[] = hsvRValues;
    final float valuesL[] = hsvLValues;

    // get a reference to the RelativeLayout so we can change the background
    // color of the Robot Controller app to match the hue detected by the RGB sensor.
    final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

    // bPrevState and bCurrState represent the previous and current state of the button.
    //boolean bPrevState = false;
    //boolean bCurrState = false;

    // while the op mode is active, loop and read the RGB data.
    // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
    while (opModeIsActive()) {
      // convert the RGB values to HSV values.
      //Color.RGBToHSV((sensorRGB.red() * 8), (sensorRGB.green() * 8), (sensorRGB.blue() * 8), hsvValues);
      Color.RGBToHSV(sensorRGBL.red() * 8, sensorRGBL.green() * 8, sensorRGBL.blue() * 8, hsvLValues);
      Color.RGBToHSV(sensorRGBR.red() * 8, sensorRGBR.green() * 8, sensorRGBR.blue() * 8, hsvRValues);

      // Make an educated guess on what the colors are
      if (sensorRGBL.blue() <= 1){
          leftGuess = "Red";
      } else if (sensorRGBL.blue() >= 4.75){
          leftGuess = "Blue";
      } else {
          leftGuess = "Gray";
      }
      if (sensorRGBR.blue() <= 1){
        rightGuess = "Red";
      } else if (sensorRGBR.blue() >= 4.75){
        rightGuess = "Blue";
      } else {
        rightGuess = "Gray";
      }


      // send the info back to driver station using telemetry function
      telemetry.addData("L blue", sensorRGBL.blue());
      telemetry.addData("L green", sensorRGBL.green());
      telemetry.addData("L red", sensorRGBL.red());
      telemetry.addData("L zClear", sensorRGBL.alpha());
      telemetry.addData("L zPrediction", leftGuess);

      telemetry.addData("R blue", sensorRGBR.blue());
      telemetry.addData("R green", sensorRGBR.green());
      telemetry.addData("R red", sensorRGBR.red());
      telemetry.addData("R zClear", sensorRGBR.alpha());
      telemetry.addData("R zPrediction", rightGuess);

      // change the background color to match the color detected by the RGB sensor.
      // pass a reference to the hue, saturation, and value array as an argument
      // to the HSVToColor method.
      relativeLayout.post(new Runnable() {
        public void run() {
          relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, valuesL));
        }
      });

      // wait a hardware cycle before iterating.
      waitOneFullHardwareCycle();
    }
  }
}
