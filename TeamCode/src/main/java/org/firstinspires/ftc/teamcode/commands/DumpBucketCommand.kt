package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystems.BucketSubsystem

@Deprecated("")
class DumpBucketCommand(var bucketSubsystem: BucketSubsystem) : CommandBase() {
    private val finished = false

    var elapsedTime: ElapsedTime = ElapsedTime()

    init {
        addRequirements(bucketSubsystem)
    }

    override fun isFinished(): Boolean {
        return elapsedTime.seconds() >= DUMP_RETRACT_TIME
    }

    override fun initialize() {
        super.initialize()
        elapsedTime.reset()
        bucketSubsystem.moveToNormalPosition(false)
    }

    override fun end(interrupted: Boolean) {
        super.end(interrupted)
        bucketSubsystem.moveToNormalPosition(true)
    }

    companion object {
        // After X seconds, move the bucket back to the original position
        const val DUMP_RETRACT_TIME: Double = 1.25
    }
}
