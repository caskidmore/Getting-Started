package org.usfirst.frc.team2823.robot;

import org.usfirst.frc.team2823.robot.Robot.ShimmyMode;

import edu.wpi.first.wpilibj.Timer;

// total 1: 7.628
// total 2: 
public class AlternateAuto implements AutoMode {
	Robot myBot;
	
	double 	stageTimeouts[] = {4.0, 2.0}; //lift back turn drive lift turn align shimmy/lift
	int     stageCounts[] = {0, 0};
	boolean stageTimeoutFailure[] = { false, false
	};	int ontarget;
	int stage = 0;
	Timer tick;
	
	public AlternateAuto(Robot myBot) {
		this.myBot = myBot;
	}
	
	public void autoInit() {
		tick = new Timer();
		tick.reset();
		tick.start();
		stage = 0;

		for (int i = 0; i < stageCounts.length; i++ )
			stageCounts[i] = 0;
		
		System.out.println("Hey, you chose the alternate autonomous mode. Good job!");
	}
	
	public void autoPeriodic() {
		
		if (stage < 0 || stage >= stageCounts.length)
			return;
		
		

		if (stageCounts[stage] == 0)
			System.out.println(stage);

	
		if (tick.get() > stageTimeouts[stage]) {
			
			System.out.printf("stage %d timed out\n", stage);

			myBot.disableAllPIDControllers();
			
			if (stageTimeoutFailure[stage]) {
				System.out.printf("stage %d failed!\n", stage);
				stage = -1;
				return;
			}
			
			System.out.println("Continuing anyway...");
			System.out.printf("%f Exiting Stage %d\n", tick.get(), stage);
			tick.reset();
			stage++;
			return;
		}
		
		//drive forward 144 inches
		if (stage == 0) {
			
			if (stageCounts[stage] == 0) {
				myBot.leftEncoder.reset();
				myBot.rightEncoder.reset();
				myBot.leftDrivingControl.setSetpoint(-myBot.myDriveDistance);
				myBot.leftDrivingControl.enable();
				myBot.rightDrivingControl.setSetpoint(myBot.myDriveDistance);
				myBot.rightDrivingControl.enable();
				ontarget = 0;
			}
			
			double l = myBot.driveEncoderToInches(myBot.leftEncoder.get());
			double r = myBot.driveEncoderToInches(myBot.rightEncoder.get());
			
			if ((Math.abs(r - (myBot.myDriveDistance)) < 2) && (Math.abs(l - (-myBot.myDriveDistance)) < 2))
				ontarget++;
			else
				ontarget = 0;
			
			if (ontarget > 10) {
				System.out.printf("%f Exiting Stage %d\n", tick.get(), stage);
				tick.reset();
				myBot.rightDrivingControl.disable();
				myBot.leftDrivingControl.disable();
				System.out.println("stage 0 succeeded!");
				tick.reset();
				stage++;
				return;
			}
		}
			
		// turn right 90 degrees
		if (stage == 1) {
			if (stageCounts[stage] == 0) {
				myBot.myGyro.reset();
				myBot.turningControl.setSetpoint(90);
				myBot.turningControl.enable();
				ontarget = 0;
				LEDSignboard.sendTextMessage("TURN ");

			}

			if (Math.abs(myBot.myGyro.getAngle() - 90) < 6)
				ontarget++;
			else
				ontarget = 0;

			if (ontarget > 10) {
				System.out.printf("%f Exiting Stage %d\n", tick.get(), stage);
				tick.reset();
				myBot.turningControl.disable();
				System.out.println("stage 1 succeeded!");
				tick.reset();
				stage++;
				return;
			}

		}
		
		stageCounts[stage]++;
	}

}
