package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.roadrunner.Action
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.SequentialAction
import com.acmerobotics.roadrunner.SleepAction
import com.acmerobotics.roadrunner.TrajectoryActionBuilder
import com.acmerobotics.roadrunner.Vector2d
import org.firstinspires.ftc.teamcode.MecanumDrive

object AutoTrajectories {
    const val WAIT_TIME: Double = 4.605

    class CompAutoTrajectorySequence(private val DRIVE: MecanumDrive) {
        // Change this to change what the robot does in auto
        private val RUN_REEL = arrayOf(
            STATES.START,
            STATES.PLACE_RUNG,
            STATES.BLOCK_LEFT,
            STATES.WAIT,
            STATES.DROP_SAMPLE,
            STATES.DROP,
            STATES.WAIT,
            STATES.TO_CORNER,
            STATES.WAIT,
            STATES.TO_RUNG,
            STATES.PLACE_RUNG,
            STATES.BLOCK_MIDDLE,
            STATES.DROP_SAMPLE,
            STATES.WAIT,
            STATES.DROP,
            STATES.END
        )

        enum class STATES {
            START,
            TO_RUNG(Pose2d(Vector2d(5.0, -35.0), Math.toRadians(90.0))),
            TO_CORNER(Pose2d(Vector2d(64.0, -65.0), Math.toRadians(0.0))),
            BLOCK_LEFT(Pose2d(Vector2d(49.0, -35.0), Math.toRadians(90.0))),
            BLOCK_MIDDLE(Pose2d(Vector2d(60.0, -35.0), Math.toRadians(90.0))),
            BLOCK_RIGHT(Pose2d(Vector2d(70.0, -35.0), Math.toRadians(90.0))),
            PARK_CORNER(Pose2d(Vector2d(43.0, -65.0), Math.toRadians(90.0))),
            PARK_SUB,
            PLACE_RUNG,
            WAIT,
            DROP_SAMPLE(Pose2d(Vector2d(43.0, -65.0), Math.toRadians(0.0))),
            DROP,
            END;

            val END_POSE: Pose2d
            private val START_POSE: Pose2d

            constructor() {
                END_POSE = INITIAL_POSE
                START_POSE = INITIAL_POSE
            }

            constructor(endPose: Pose2d) {
                END_POSE = endPose
                START_POSE = INITIAL_POSE
            }

            constructor(startPose: Pose2d, endPose: Pose2d) {
                START_POSE = startPose
                END_POSE = endPose
            }

            fun get_END_POSE(): Pose2d {
                return END_POSE
            }

            fun get_START_POSE(): Pose2d {
                return START_POSE
            }
        }

        fun build(): SequentialAction {
            val actions = ArrayList<Action>()
            var previousState = STATES.START
            for (state in RUN_REEL) {
                when (state) {
                    STATES.START -> actions.add(generateStartTrajectory()!!.build())
                    STATES.WAIT -> actions.add(SleepAction(1.605))
                    STATES.PLACE_RUNG -> actions.add(generateToRungTrajectory(previousState)!!.build())
                    STATES.TO_CORNER -> actions.add(generateToCorner(previousState)!!.build())
                    STATES.END -> return SequentialAction(actions)
                    else -> return SequentialAction(actions)
                }
                previousState = state
            }
            return SequentialAction(actions)
        }

        private fun generateStartTrajectory(): TrajectoryActionBuilder? {
            START = DRIVE.actionBuilder(STATES.START.get_START_POSE())
                .lineToX(STATES.TO_RUNG.END_POSE.position.x)
            return START
        }

        private fun generateToRungTrajectory(previousState: STATES): TrajectoryActionBuilder? {
            TO_RUNG = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineToConstantHeading(
                    STATES.TO_RUNG.END_POSE.position,
                    STATES.TO_RUNG.END_POSE.heading
                )
            return TO_RUNG
        }

        private fun generateToCorner(previousState: STATES): TrajectoryActionBuilder? {
            val pos = STATES.TO_CORNER.END_POSE.position
            val heading = STATES.TO_CORNER.END_POSE.heading
            TO_CORNER = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineToConstantHeading(Vector2d(40.0, pos.y), heading)
                .waitSeconds(2.0)
                .splineToConstantHeading(pos, heading)

            return TO_CORNER
        }

        companion object {
            val INITIAL_POSE: Pose2d = Pose2d(Vector2d(5.0, -65.0), Math.toRadians(90.0))

            var START: TrajectoryActionBuilder? = null
            var TO_RUNG: TrajectoryActionBuilder? = null
            var TO_CORNER: TrajectoryActionBuilder? = null
            var TO_BLOCK_LEFT: TrajectoryActionBuilder? = null
            var TO_BLOCK_MIDDLE: TrajectoryActionBuilder? = null
            var TO_BLOCK_RIGHT: TrajectoryActionBuilder? = null
        }
    }
}
