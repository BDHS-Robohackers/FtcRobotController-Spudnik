package org.firstinspires.ftc.teamcode.commands;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.Subsystem;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.BucketSubsystem;

import java.util.Set;

@Deprecated
public class DumpBucketCommand extends CommandBase {
    private boolean finished = false;

    // After X seconds, move the bucket back to the original position
    public static final double DUMP_RETRACT_TIME = 1.25d;

    BucketSubsystem bucketSubsystem;
    ElapsedTime elapsedTime;

    public DumpBucketCommand(BucketSubsystem bucketSubsystem) {
        this.bucketSubsystem = bucketSubsystem;
        this.elapsedTime = new ElapsedTime();
        addRequirements(bucketSubsystem);
    }

    @Override
    public boolean isFinished() {
        return elapsedTime.seconds() >= DUMP_RETRACT_TIME;
    }

    @Override
    public void initialize() {
        super.initialize();
        elapsedTime.reset();
        bucketSubsystem.moveToNormalPosition(false);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        bucketSubsystem.moveToNormalPosition(true);
    }

}
